package com.wang.redis.Controller;

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
        try {
            Thread.sleep(2000);
            connection.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
