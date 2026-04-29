package cn.icexmoon.netty.time;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @ClassName TimeClient
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/28 18:04
 * @Version 1.0
 */
@Slf4j
public class TimeClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", TimeServer.PORT)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    writer.println("QUERY TIME ORDER");
                    log.info("Send order 2 server succeed.");
                    String response = reader.readLine();
                    log.info("Now time is: {}", response);
                }
            }
        }
    }
}
