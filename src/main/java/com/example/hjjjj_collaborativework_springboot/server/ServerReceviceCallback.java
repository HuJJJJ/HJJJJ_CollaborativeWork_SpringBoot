package com.example.hjjjj_collaborativework_springboot.server;

import com.alibaba.fastjson.JSONObject;
import com.example.hjjjj_collaborativework_springboot.Manager.SocketClientGroup;
import com.example.hjjjj_collaborativework_springboot.entity.SocketClient;
import com.example.hjjjj_collaborativework_springboot.handle.PackHandle;
import com.example.hjjjj_collaborativework_springboot.pojo.MessageRequest;

import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import static com.example.hjjjj_collaborativework_springboot.pojo.MessageType.*;

public class ServerReceviceCallback {


    public static String msgCenter(MessageRequest msg, Socket socket) {
        JSONObject json = msg.getJsonObject();
        String cmd = json.getString("Cmd");
        String replyMsg = null;
        switch (cmd) {
            case CONNECT_SERVER:
                replyMsg = connServerHandle(json, socket);
                break;
            case APPLY_CONNECT_CLIENT:
                replyMsg = applyConnectClientHandle(json);
                break;
            case REPLY_CONNECT_CLIENT:
                replyMsg = replyConnectClientHandle(json);
                break;
            case GETONLINEDEVICES:
                replyMsg = getOnlineDevicesHandle(socket);
                break;
            case EXCHANGECLIPBOARD:
                replyMsg = exChangeClipboardHandle(json, socket);
                break;
            case FILETRANSFERREQUEST:
            case FILETRANSFERRESPONSE:
            case FILETRANSFERRING:
                replyMsg = fileTransferHandle(msg, socket);
                break;
            case CLIENTDISCONNECTED:
                replyMsg = clientDisConnectedHandle(msg,socket);
        }
        return replyMsg;
    }

    private static  String clientDisConnectedHandle(MessageRequest msg, Socket socket){
        try {
            var client = SocketClientGroup.getClientBySocket(socket);
            var targetClient = client.getConnectClient();
            client.setConnectClient(null);
            targetClient.setConnectClient(null);
            if (client != null) {
                return relayMessage(targetClient, msg.getJsonObject());
            } else return receivedACK("Received_ACK", false);

        } catch (Exception ex) {
            return receivedACK("Received_ACK", false);
        }
    }

    private static String fileTransferHandle(MessageRequest msg, Socket socket) {

        try {
            var client = SocketClientGroup.getClientBySocket(socket);
            var targetClient = client.getConnectClient();
              var bytes = PackHandle.Pack(msg.getJsonObject().toJSONString(), msg.getPayload());
            if (client != null) {
                OutputStream writer = targetClient.getSocket().getOutputStream();
                writer.write(bytes);
                writer.flush();
            }
            return receivedACK("Received_ACK", true);
        } catch (Exception ex) {
            return receivedACK("Received_ACK", false);
        }
    }

    private static String exChangeClipboardHandle(JSONObject msg, Socket socket) {
        var client = SocketClientGroup.getClientBySocket(socket);
        var targetClient = client.getConnectClient();
        return relayMessage(targetClient, msg);
    }

    //客户端连接确认
    private static String connServerHandle(JSONObject msg, Socket socket) {

        SocketClient client = msg.toJavaObject(SocketClient.class);
        client.setSocket(socket);
        SocketClientGroup.joinGroup(client);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Result", "true");
        jsonObject.put("Cmd", CONNECT_SERVER);
        return jsonObject.toJSONString();
    }

    //获取组内在线设备
    private static String getOnlineDevicesHandle(Socket socket) {
        List<SocketClient> clients = SocketClientGroup.getOnlineDevices(socket);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Cmd", GETONLINEDEVICES);
        jsonObject.put("Clients", clients);
        return jsonObject.toJSONString();
    }


    ///客户端回复连接申请
    private static String applyConnectClientHandle(JSONObject msg) {
        var client = SocketClientGroup.getClientByClientID(msg.getString("TargetClientID"));
        return relayMessage(client, msg);
    }

    ///客户端发出对其他客户端的连接申请
    private static String replyConnectClientHandle(JSONObject msg) {
        var sourceClient = SocketClientGroup.getClientByClientID(msg.getString("SourceClientID"));
        var targetClient = SocketClientGroup.getClientByClientID(msg.getString("TargetClientID"));
        sourceClient.setConnectClient(targetClient);
        targetClient.setConnectClient(sourceClient);
        return relayMessage(sourceClient, msg);
    }

    public static String relayMessage(SocketClient client, JSONObject msg) {
        try {

            if (client != null) {
                OutputStream writer = client.getSocket().getOutputStream();
                var response = PackHandle.Pack(JSONObject.toJSONString(msg), new byte[0]);
                writer.write(response);

                writer.flush();
            }
            return receivedACK("Received_ACK", true);
        } catch (Exception ex) {
            return receivedACK("Received_ACK", false);
        }
    }

    //确认接收消息
    public static String receivedACK(String cmd, boolean result) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Cmd", cmd);
        jsonObject.put("Result", result);
        return JSONObject.toJSONString(jsonObject);
    }

}
