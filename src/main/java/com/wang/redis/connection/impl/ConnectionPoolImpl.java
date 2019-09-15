package com.wang.redis.connection.impl;

import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.config.RedisWangProperties;
import com.wang.redis.connection.Connection;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description redis连接池
 * @author Jianxin Wang
 * @date 2019-08-27
 */
@Service
public class ConnectionPoolImpl extends DefaultAbstractPoolImpl {
    private static final Logger logger = Logger.getLogger(ConnectionPoolImpl.class);

    private String address;
    private int port;

    public ConnectionPoolImpl(RedisWangProperties redisWangProperties){
        if(this.port <= 0){
            throw new RedisWangException("[redis-wang]redis的端口设置错误");
        }
        this.address = redisWangProperties.getAddress();
        this.port = redisWangProperties.getPort();
        logger.info("[redis-wang]redis配置完成");
        //尝试连接是否配置数据正确
        connection();
    }

    public  Connection connection(){
        Connection connection = null;
        try {
            connection = new ConnectionImpl(address,port);
        } catch (IOException e) {
            logger.error("[wang-redis]连接错误");
        }
        return connection;
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
