package cn.icexmoon.netty.time;

import cn.icexmoon.netty.time.handler.TimeServerHandler;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName TimeServer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/28 17:54
 * @Version 1.0
 */
@Slf4j
public class TimeServer {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        new Thread(new TimeServerHandler()).start();
    }
}
