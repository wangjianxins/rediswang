package com.wang.redis.transmission;

public class HostInfo {
    public String address;
    public int port;

    public HostInfo(){}
    public HostInfo(String address,int port){
        this.address = address;
        this.port  = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adrdess) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
