package cn.icexmoon.netty.filerserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * HTTP 文件服务器处理器
 * <p>
 * 负责处理 HTTP 文件请求，支持：
 * <ul>
 *     <li>文件下载（支持分块传输）</li>
 *     <li>目录浏览（生成 HTML 目录列表）</li>
 *     <li>MIME 类型自动检测</li>
 *     <li>路径安全检查（防止目录遍历攻击）</li>
 *     <li>Keep-Alive 连接管理</li>
 * </ul>
 *
 * @author icexmoon@qq.com
 * @version 1.0
 * @since 2026/5/12
 */
@Slf4j
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    /** 文件服务器根目录路径 */
    public static final String HTTP_FILE_SERVER_ROOT_DIR = "D:\\download";

    /**
     * 非法文件名匹配模式
     * <p>
     * 匹配包含非字母、数字、下划线、连字符、点号的文件名
     * 用于过滤不安全或特殊字符的文件名
     */
    private static final Pattern INALLOWED_FILE_NAME = Pattern.compile("[^A-Za-z0-9_\\-\\.]");

    /**
     * 处理 HTTP 请求的核心方法
     * <p>
     * 处理流程：
     * <ol>
     *     <li>验证请求解码是否成功</li>
     *     <li>检查 HTTP 方法是否为 GET</li>
     *     <li>清理和验证 URI 路径</li>
     *     <li>检查文件是否存在和可访问</li>
     *     <li>如果是目录，返回目录列表</li>
     *     <li>如果是文件，启动分块传输</li>
     * </ol>
     *
     * @param ctx     通道上下文
     * @param request 完整的 HTTP 请求对象
     * @throws Exception 处理过程中的异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        log.debug("收到请求: {} {}", request.method(), request.uri());
        
        // 步骤 1: 验证请求解码结果
        if (!request.decoderResult().isSuccess()) {
            log.warn("请求解码失败: {}", request.uri());
            sendError(ctx, BAD_REQUEST);
            return;
        }
        // 步骤 2: 验证 HTTP 方法（仅支持 GET）
        if (!request.method().equals(HttpMethod.GET)) {
            log.warn("不支持的 HTTP 方法: {}", request.method());
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }
        // 步骤 3: 清理和验证 URI
        final String uri = request.uri();
        final String path = sanitizeUri(uri);
        if (path == null) {
            log.warn("非法 URI: {}", uri);
            sendError(ctx, FORBIDDEN);
            return;
        }
        // 步骤 4: 检查文件是否存在且可访问
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            log.debug("文件不存在或已隐藏: {}", path);
            sendError(ctx, NOT_FOUND);
            return;
        }
        // 步骤 5: 如果是目录，返回 HTML 格式的目录列表
        if (file.isDirectory()) {
            log.info("访问目录: {}", path);
            sendListing(ctx, file);
            return;
        }
        // 步骤 6: 验证是否为普通文件
        if (!file.isFile()) {
            log.warn("不是有效文件: {}", path);
            sendError(ctx, FORBIDDEN);
            return;
        }
        // 步骤 7: 使用 RandomAccessFile 读取文件并启动分块传输
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = raf.length();
            log.info("开始传输文件: {}, 大小: {} bytes", path, fileLength);
            
            // 创建 HTTP 响应头
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            // 设置内容长度和 MIME 类型
            HttpUtil.setContentLength(response, fileLength);
            setContentTypeHeader(response, file);
            
            // 根据客户端请求设置 Keep-Alive 连接
            if (HttpUtil.isKeepAlive(request)) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            // 发送响应头
            ctx.write(response);
            
            // 使用 ChunkedFile 进行分块传输（8KB 每块），避免大文件占用过多内存
            ChannelFuture channelFuture = ctx.write(new ChunkedFile(raf, 0, fileLength, 8192), ctx.newProgressivePromise());
            // 添加传输进度监听器
            channelFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                    log.info("文件传输完成: {}", path);
                }

                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                    if (total < 0) {
                        log.debug("传输进度: {} bytes", progress);
                    } else {
                        log.debug("传输进度: {} / {} bytes", progress, total);
                    }
                }
            });
            // 发送最后一个空内容块，标志着消息体结束
            ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            
            // 如果不支持 Keep-Alive，传输完成后关闭连接
            if (!HttpUtil.isKeepAlive(request)) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception e) {
            log.error("文件传输失败: {}", path, e);
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 生成并返回目录的 HTML 列表页面
     * <p>
     * 当用户访问一个目录时，此方法会：
     * <ul>
     *     <li>扫描目录中的所有文件和子目录</li>
     *     <li>过滤隐藏文件和包含非法字符的文件</li>
     *     <li>生成 HTML 格式的目录列表页面</li>
     *     <li>为每个条目创建相对路径链接</li>
     * </ul>
     *
     * @param ctx  通道上下文
     * @param file 要列出内容的目录文件对象
     */
    private void sendListing(ChannelHandlerContext ctx, File file) {
        log.debug("生成目录列表: {}", file.getPath());
        
        // 构建目录列表的 HTML 页面
        StringBuilder buf = new StringBuilder();
        String dirPath = file.getPath();
        
        // 计算当前目录相对于根目录的相对路径（用于调试和日志）
        String relativePath = "";
        if (!dirPath.equals(HTTP_FILE_SERVER_ROOT_DIR)) {
            relativePath = dirPath.substring(HTTP_FILE_SERVER_ROOT_DIR.length());
            // 确保以 / 开头
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
        }

        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>Directory Listing: ");
        buf.append(dirPath);
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>Directory Listing: ");
        buf.append(dirPath);
        buf.append("</h3>\r\n");
        buf.append("<ul>\r\n");
        // 如果不是根目录，显示返回上级目录的链接
        if (!relativePath.isEmpty()) {
            buf.append("<li><a href=\"../\">..</a></li>\r\n");
        }

        // 安全地获取目录内容，处理可能的 null 返回值
        File[] files = file.listFiles();
        if (files == null) {
            log.warn("无法读取目录内容: {}", dirPath);
            files = new File[0];
        }
        
        // 遍历目录中的所有文件和子目录
        int fileCount = 0;
        for (File f : files) {
            if (f.isHidden() || !f.canRead()) {
                log.debug("跳过隐藏或不可读文件: {}", f.getName());
                continue;
            }

            String name = f.getName();
            // 如果文件名包含非法字符，则跳过
            if (INALLOWED_FILE_NAME.matcher(name).find()) {
                log.debug("跳过包含非法字符的文件名: {}", name);
                continue;
            }

            // 构建相对路径链接（浏览器会自动基于当前 URL 解析）
            String linkPath = name;
            // 目录链接末尾添加 / 以便识别
            if (f.isDirectory()) {
                linkPath += "/";
            }
            
            buf.append("<li><a href=\"");
            buf.append(linkPath);
            buf.append("\">");
            buf.append(name);
            if (f.isDirectory()) {
                buf.append("/");
            }
            buf.append("</a></li>\r\n");
            fileCount++;
        }

        buf.append("</ul>\r\n");
        buf.append("</body></html>\r\n");

        // 创建 HTTP 响应
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        HttpUtil.setContentLength(response, buffer.readableBytes());

        log.info("返回目录列表: {}, 包含 {} 个条目", dirPath, fileCount);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 设置 HTTP 响应的 Content-Type 头部
     * <p>
     * 根据文件扩展名自动探测 MIME 类型：
     * <ul>
     *     <li>使用 JDK 的 Files.probeContentType() 进行探测</li>
     *     <li>如果探测失败，使用 application/octet-stream 作为默认值</li>
     * </ul>
     *
     * @param response HTTP 响应对象
     * @param file     要设置类型的文件对象
     */
    private void setContentTypeHeader(HttpResponse response, File file) {
        String contentType = null;
        try {
            // 使用 JDK 自带的 FileNameMap 来探测 MIME 类型
            contentType = Files.probeContentType(file.toPath());
            log.debug("文件 {} 的 MIME 类型: {}", file.getName(), contentType);
        } catch (Exception e) {
            log.warn("无法探测文件 {} 的 MIME 类型: {}", file.getName(), e.getMessage());
        }

        // 如果无法探测到 MIME 类型，使用默认的二进制流类型
        if (contentType == null) {
            contentType = "application/octet-stream";
            log.debug("使用默认 MIME 类型: application/octet-stream");
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    /**
     * 清理和验证 URI 路径
     * <p>
     * 执行以下安全检查：
     * <ul>
     *     <li>URL 解码（处理 %XX 编码）</li>
     *     <li>验证 URI 格式（必须以 / 开头）</li>
     *     <li>防止目录遍历攻击（检测 .. 和 .）</li>
     *     <li>统一文件分隔符</li>
     *     <li>拼接完整的文件系统路径</li>
     * </ul>
     *
     * @param uri 原始 URI 字符串
     * @return 清理后的完整文件路径，如果 URI 不安全则返回 null
     */
    private String sanitizeUri(String uri) {
        log.debug("处理 URI: {}", uri);
        
        // 步骤 1: URL 解码，将 %XX 编码转换为原始字符
        try {
            uri = QueryStringDecoder.decodeComponent(uri, CharsetUtil.UTF_8);
        } catch (IllegalArgumentException e) {
            log.warn("URI 解码失败: {}", uri);
            return null;
        }

        // 步骤 2: 验证 URI 格式
        if (!uri.startsWith("/")) {
            log.warn("URI 不以 / 开头: {}", uri);
            return null;
        }

        // 步骤 3: 统一文件分隔符（将 / 转换为系统分隔符）
        uri = uri.replace('/', File.separatorChar);

        // 步骤 4: 安全检查 - 防止目录遍历攻击
        // 检测 ../ 或 ..\ 等路径遍历模式
        if (uri.contains(File.separator + ".")
                || uri.contains("." + File.separator)
                || uri.equals(".")
                || uri.equals("..")) {
            log.warn("检测到不安全的路径: {}", uri);
            return null;
        }

        // 步骤 5: 清理末尾的文件分隔符（根目录除外）
        final int end = uri.length() - 1;
        if (end > 0 && uri.charAt(end) == File.separatorChar) {
            uri = uri.substring(0, end);
        }

        // 步骤 6: 拼接根目录和相对路径，生成完整的文件系统路径
        String path = HTTP_FILE_SERVER_ROOT_DIR + uri;
        log.debug("解析后的文件路径: {}", path);
        return path;
    }

    /**
     * 发送 HTTP 错误响应
     * <p>
     * 创建包含错误信息的纯文本响应，并在发送后关闭连接
     *
     * @param ctx    通道上下文
     * @param status HTTP 响应状态码
     */
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        log.debug("发送错误响应: {}", status);
        
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
