package com.wang.redis.connection.impl;

import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.connection.Connection;
import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description 连接实现类
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public class ConnectionImpl implements Connection {

    private static final Logger logger = Logger.getLogger(ConnectionImpl.class);

    private RedisInputStream inputStream;

    private RedisOutputStream outputStream;

    private Socket socket = new Socket();

    public ConnectionImpl(String address,int port) throws IOException {
        socket.setKeepAlive(true);
        try {
            socket.connect(new InetSocketAddress(address, port));
        }catch (Exception e){
            throw new RedisWangException("[redis-wang]连接redis失败："+e.getMessage());
            //激活重试连接
        }
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
            logger.info("正式关闭连接");
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
