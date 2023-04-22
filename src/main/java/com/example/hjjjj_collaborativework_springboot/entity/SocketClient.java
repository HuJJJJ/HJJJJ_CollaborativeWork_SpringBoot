package com.example.hjjjj_collaborativework_springboot.entity;

import java.net.Socket;

public class SocketClient {
    private String ClientID;
    private String GroupName;
    private String UserName;
    private transient String Password;
    private transient Socket socket;
    private transient SocketGroup group;
    private transient SocketClient ConnectClient;

    public SocketClient getConnectClient() {
        return ConnectClient;
    }

    public void setConnectClient(SocketClient connectClient) {
        ConnectClient = connectClient;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public SocketGroup getGroup() {
        return group;
    }

    public void setGroup(SocketGroup group) {
        this.group = group;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
}
