package com.example.hjjjj_collaborativework_springboot.entity;

import java.util.ArrayList;
import java.util.List;

public class SocketGroup {

    public SocketGroup(){
        Clients = new ArrayList<>();
    }

    private String GroupName;
    private List<SocketClient> Clients;

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public List<SocketClient> getClients() {
        return Clients;
    }

    public void setClients(List<SocketClient> clients) {
        Clients = clients;
    }
}
