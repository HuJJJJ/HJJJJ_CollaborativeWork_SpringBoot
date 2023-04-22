package com.example.hjjjj_collaborativework_springboot.server;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class SocketServer {

    private final int port = 1883;
    public static ServerSocket serverSocket = null;

    private static final ThreadPoolExecutor threadpool = new ThreadPoolExecutor(15, 15,
            10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Bean
    public void socketCreate() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("socket服务端开启。端口:" + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("接收到客户端socket" + socket.getRemoteSocketAddress());
                threadpool.execute(new ServerReceiveThread(socket));
            }
        } catch (IOException e) {
            System.out.println("socket服务启动异常");
            e.printStackTrace();
        }
    }

}
