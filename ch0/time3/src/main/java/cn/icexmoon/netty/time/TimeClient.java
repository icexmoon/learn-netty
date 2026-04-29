package cn.icexmoon.netty.time;

import cn.icexmoon.netty.time.handler.ClientHandler;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;


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
        new Thread(new ClientHandler()).start();
    }
}
