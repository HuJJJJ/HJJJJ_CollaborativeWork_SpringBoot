package com.example.hjjjj_collaborativework_springboot.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hjjjj_collaborativework_springboot.Manager.SocketClientGroup;
import com.example.hjjjj_collaborativework_springboot.Util.Utils;
import com.example.hjjjj_collaborativework_springboot.handle.PackHandle;
import com.example.hjjjj_collaborativework_springboot.pojo.MessageRequest;
import com.example.hjjjj_collaborativework_springboot.pojo.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerReceiveThread implements Runnable {
    private Socket socket;

    private static Logger log = LoggerFactory.getLogger(ServerReceiveThread.class);

    public ServerReceiveThread(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            //输出流发送数据
            OutputStream writer = socket.getOutputStream();
            // PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //输入流接收数据
            // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            // InputStream inputStream = socket.getInputStream();
            // ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            // int b;


            DataInputStream dis = new DataInputStream(socket.getInputStream());
            var length = new byte[4];
            while (true) {
                long begin = System.currentTimeMillis();
                dis.read(length);
                int jsonLength = Utils.bytesToInt(length, 0);
                dis.read(length);
                int payloadLength = Utils.bytesToInt(length, 0);
                byte[] fileBytes = new byte[payloadLength];
                dis.readFully(fileBytes);
                byte[] stringMessageBytes = new byte[jsonLength];
                dis.readFully(stringMessageBytes);
                String stringMessage = new String(stringMessageBytes);
                System.out.println(stringMessage);
                MessageRequest request = new MessageRequest();
                JSONObject jsonObject = JSONObject.parseObject(JSON.parse(stringMessage).toString(), JSONObject.class);
                request.setJsonStringLength(jsonLength);
                request.setPayloadLength(payloadLength);
                request.setJsonObject(jsonObject);
                request.setPayload(fileBytes);
                String message = ServerReceviceCallback.msgCenter(request, socket);
                var response = PackHandle.Pack(message, new byte[0]);
                writer.write(response);
                writer.flush();
                long end = System.currentTimeMillis();
                System.out.println("一片：" + (end - begin) + "ms");
            }

        } catch (Exception e) {

            log.info("接收数据异常socket关闭");
            e.printStackTrace();
        } finally {
            //通知客户端的连接方客户端已退出，并从服务器列表中移除客户端
            var client = SocketClientGroup.getClientBySocket(socket);
            var connClient = client.getConnectClient();
            if (connClient != null) {
                JSONObject obj = new JSONObject();
                obj.put("Cmd", MessageType.CLIENTDISCONNECTED);
                ServerReceviceCallback.relayMessage(connClient, obj);
            }
            SocketClientGroup.removeSocket(socket);


            log.info("数据异常数据要怎么保留");
        }
    }

}
