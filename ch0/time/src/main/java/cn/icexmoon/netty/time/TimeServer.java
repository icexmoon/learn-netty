package cn.icexmoon.netty.time;

import cn.icexmoon.netty.time.handler.TimeServerHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
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
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            log.info("TimeServer start at {}", PORT);
            while (true) {
                Socket accept = serverSocket.accept();
                log.debug("TimeServer accept a new connection from {}:{}", 
                    accept.getInetAddress().getHostAddress(), accept.getPort());
                new Thread(new TimeServerHandler(accept)).start();
            }
        } catch (Exception e) {
            log.error("TimeServer error", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    log.error("Close server socket error", e);
                }
            }
        }
    }
}
