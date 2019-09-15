package com.wang.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

//读取配置文件的配置

@ConfigurationProperties(prefix = "redis")
public class RedisWangProperties {

    private String address;
    private int port;
    //哨兵host:port信息，逗号分割
    private String sentinels;
    private String masterName;


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


    public String getSentinels() {
        return sentinels;
    }

    public void setSentinels(String sentinels) {
        this.sentinels = sentinels;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }
}
