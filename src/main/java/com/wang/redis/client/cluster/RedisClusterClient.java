package com.wang.redis.client.cluster;

import com.wang.redis.client.host.DefaultExecute;
import com.wang.redis.connection.ConnectionPool;

public class RedisClusterClient extends DefaultExecute {

    private ConnectionPool connectionPool;

    private String address;
    private int port;

    public RedisClusterClient(ConnectionPool connectionPool){
        super(connectionPool);
        this.connectionPool = connectionPool;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
