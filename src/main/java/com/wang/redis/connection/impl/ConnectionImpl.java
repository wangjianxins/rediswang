package com.wang.redis.connection.impl;

import com.wang.redis.connection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description 连接实现类
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public class ConnectionImpl implements Connection {

    private Socket socket = new Socket();

    public ConnectionImpl(String address,int port) throws IOException {
        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(address, port));
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
