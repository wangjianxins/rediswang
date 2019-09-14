package com.wang.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

//读取配置文件的配置

@ConfigurationProperties(prefix = "redis")
public class RedisWangProperties {

    private String address;
    private int port;
    private String password;

    public RedisWangProperties(String address,int port){
        this.address = address;
        this.port = port;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
