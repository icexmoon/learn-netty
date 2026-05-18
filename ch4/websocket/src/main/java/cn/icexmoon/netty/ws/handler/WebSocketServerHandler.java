package cn.icexmoon.netty.ws.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @ClassName WebSocketServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/18 09:19
 * @Version 1.0
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            handleHttpRequest(ctx, request);
        } else if (msg instanceof WebSocketFrame frame) {
            handleWebSocketFrame(ctx, frame);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            // 是关闭链路的请求帧，关闭链路
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否为 PING
        if (frame instanceof PingWebSocketFrame) {
            // 返回 PONG
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            // 这个示例中不支持二进制消息
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported",
                    frame.getClass().getName()));
        }
        // 返回响应消息
        String requestMsg = ((TextWebSocketFrame) frame).text();
        log.trace("收到消息：{}", requestMsg);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(
                "欢迎使用 netty websocket 服务，现在时刻："
                        + new Date().toString()));
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (!request.decoderResult().isSuccess()
                || !"websocket".equals(request.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 是握手请求，创建WebSocketServerHandler
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8080", null, false);
        handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        // 如果响应状态码不是200，则添加错误信息
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), StandardCharsets.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }
        
        // 设置响应头
        response.headers().setInt(io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        
        // 发送响应并关闭连接
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
