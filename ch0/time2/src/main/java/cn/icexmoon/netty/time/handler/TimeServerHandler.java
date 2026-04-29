package cn.icexmoon.netty.time.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @ClassName TimeServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/28 17:55
 * @Version 1.0
 */
@Slf4j
public class TimeServerHandler implements Runnable {
    private Socket accept;

    public TimeServerHandler(Socket accept) {
        this.accept = accept;
    }

    @Override
    public void run() {
        log.debug("Handling connection from {}:{}",
                accept.getInetAddress().getHostAddress(), accept.getPort());
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()))) {
                try (PrintWriter writer = new PrintWriter(accept.getOutputStream(), true)) {
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        log.trace("Received: {}", line);
                        String resp;
                        if ("QUERY TIME ORDER".equalsIgnoreCase(line)) {
                            long now = System.currentTimeMillis();
                            resp = Long.toString(now);
                        }
                        else {
                            resp = "BAD ORDER";
                        }
                        log.trace("Send: {}", resp);
                        writer.println(resp);
                    }

                }
            }
        } catch (Exception e) {
            log.error("Error handling connection from {}:{}",
                    accept.getInetAddress().getHostAddress(), accept.getPort(), e);
        } finally {
            try {
                accept.close();
                log.debug("Connection closed from {}:{}",
                        accept.getInetAddress().getHostAddress(), accept.getPort());
            } catch (Exception e) {
                log.error("Error closing connection", e);
            }
        }
    }
}
