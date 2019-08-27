package com.wang.redis.Controller;

import com.wang.redis.Command.AbstractCommand;
import com.wang.redis.Command.RedisWangClinet;
import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RedisController {

    @Autowired
    private ConnectionPool connectionPool;

    @RequestMapping("/redis")
    public void redis() throws IOException {
        Connection connection = connectionPool.getConnection();
        RedisWangClinet redisWangClinet = new RedisWangClinet(connection);
        redisWangClinet.set("wang","test");

    }
}
