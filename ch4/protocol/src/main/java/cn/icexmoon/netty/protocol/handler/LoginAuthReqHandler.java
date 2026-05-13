package cn.icexmoon.netty.protocol.handler;

import cn.icexmoon.netty.protocol.pojo.Header;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LoginAuthReqHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 10:47
 * @Version 1.0
 */
@Slf4j
public class LoginAuthReqHandler extends SimpleChannelInboundHandler<NettyMessage> {
    private static String[] whiteList = {"127.0.0.1", "192.168.1.100"};
    private static Set<String> loginSet = ConcurrentHashMap.newKeySet();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage nettyMessage) throws Exception {
        if (nettyMessage == null) {
            return;
        }
        if (nettyMessage.getHeader() == null) {
            return;
        }
        if (nettyMessage.getHeader().getType() != Header.Type.LOGIN_AUTH_REQ) {
            ctx.fireChannelRead(nettyMessage);
            return;
        }
        // 获取客户端 ip (带端口号)
        String ipAndPort = ctx.channel().remoteAddress().toString();
        log.debug("客户端ip和端口:{}", ipAndPort);
        // 获取只有 host 的 ip
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = socketAddress.getAddress().getHostAddress();
        log.debug("客户端ip:{}", ip);
        if (!inWhiteList(ip)) {
            log.error("客户端{},不在白名单中", ip);
            ctx.writeAndFlush(buildLoginAuthResp((byte) -1));
            return;
        }
        if (loginSet.contains(ipAndPort)) {
            log.error("客户端{},已经登录，不能重复登录", ipAndPort);
            ctx.writeAndFlush(buildLoginAuthResp((byte) -1));
            return;
        }
        ctx.writeAndFlush(buildLoginAuthResp((byte) 0));
        loginSet.add(ipAndPort);
        log.info("客户端{},握手成功", ipAndPort);
    }

    private static boolean inWhiteList(String ip) {
        for (String s : whiteList) {
            if (s.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    private static NettyMessage buildLoginAuthResp(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(Header.Type.LOGIN_AUTH_RESP);
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 与客户端断开连接后，移除客户端的已登录信息
        loginSet.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
