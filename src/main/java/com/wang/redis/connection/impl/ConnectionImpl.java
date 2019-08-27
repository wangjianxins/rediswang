package com.wang.redis.connection.impl;

import com.wang.redis.connection.Connection;
import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description 连接实现类
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public class ConnectionImpl implements Connection {

    private RedisInputStream inputStream;

    private RedisOutputStream outputStream;

    private Socket socket = new Socket();

    public ConnectionImpl(String address,int port) throws IOException {
        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(address, port));
        outputStream = new RedisOutputStream(socket.getOutputStream());
        inputStream = new RedisInputStream(socket.getInputStream());
    }

    @Override
    public RedisInputStream getInputStream() {
        return inputStream;
    }/**/

    @Override
    public RedisOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean isClosed() {
        return socket.isClosed();
    }
}
