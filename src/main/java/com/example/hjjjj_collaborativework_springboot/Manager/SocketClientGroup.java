package com.example.hjjjj_collaborativework_springboot.Manager;

import com.example.hjjjj_collaborativework_springboot.entity.SocketClient;
import com.example.hjjjj_collaborativework_springboot.entity.SocketGroup;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SocketClientGroup {
    public static List<SocketGroup> Groups = new ArrayList<>();

    public static boolean joinGroup(SocketClient client) {
        try {
            SocketGroup group = Groups.stream().filter(e -> e.getGroupName().equals(client.getGroupName())).findFirst().orElse(null);
            if (group != null) {
                client.setGroup(group);
                group.getClients().add(client);
            } else {
                SocketGroup newGroup = new SocketGroup();
                client.setGroup(newGroup);
                newGroup.getClients().add(client);
                newGroup.setGroupName(client.getGroupName());
                Groups.add(newGroup);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    //离开分组
    public static boolean leaveGroup(SocketClient client) {

        try {
            SocketGroup group = Groups.stream().filter(e -> e.getGroupName().equals(client.getGroupName())).findFirst().orElse(null);
            if (!group.equals(null)) {
                group.getClients().remove(client);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    ///获取组内在线设备
    public static List<SocketClient> getOnlineDevices(Socket socket) {
        for (var group : Groups) {
            var client = group.getClients().stream().filter(e -> e.getSocket().equals(socket)).findFirst().orElse(null);
            if (client != null) {
                return group.getClients().stream().filter(e -> !e.equals(client)).collect(Collectors.toList());
            }
        }
        return null;
    }

    ///查找client
    public static SocketClient getClientBySocket(Socket socket) {
        for (var group : Groups) {
            var client = group.getClients().stream().filter(e -> e.getSocket().equals(socket)).findFirst().orElse(null);
            if (client != null) {
                return client;
            }
        }
        return null;
    }

    public static SocketClient getClientByClientID(String ClientID) {
        for (var group : Groups) {
            var client = group.getClients().stream().filter(e -> e.getClientID().equals(ClientID)).findFirst().orElse(null);
            if (client != null) {
                return client;
            }
        }
        return null;
    }

    public static void removeSocket(Socket socket) {

        //移除socket
        for (var group : Groups) {
            var client = group.getClients().stream().filter(e -> e.getSocket().equals(socket)).findFirst().orElse(null);
            if (client != null) {
                var connectClient = group.getClients().stream().filter(e -> client.equals(e.getConnectClient())).findFirst().orElse(null);
                if (connectClient != null) {
                    connectClient.setConnectClient(null);
                }
                group.getClients().remove(client);
            }
        }


    }
}
