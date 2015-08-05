package com.huzefagadi.rashida.printform.bean;

/**
 * Created by Rashida on 13/07/15.
 */
public class ServerBean {

    private int id;
    private String serverName;

    public ServerBean(int id, String serverName) {
        this.id = id;
        this.serverName = serverName;
    }

    public ServerBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
