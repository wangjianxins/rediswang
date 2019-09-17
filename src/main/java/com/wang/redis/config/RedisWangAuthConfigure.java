package com.wang.redis.config;
import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.client.host.RedisWangClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;


//自动装配类
@Configuration
@ConditionalOnClass(RedisWangClient.class)
@EnableConfigurationProperties(RedisWangProperties.class)
public class RedisWangAuthConfigure {
    private static final Logger logger = Logger.getLogger(RedisWangAuthConfigure.class);

    @Autowired
    private RedisWangProperties redisWangProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "redis",value = "enabled",havingValue = "true")
    public RedisWangClient getClient(){
        String type = redisWangProperties.getType();
        if(null == type){
            throw new RedisWangException("配置参数redis.type未配置");
        }
        RedisWangClient redisWangClient = null;
        switch (type){
            case "host":
                redisWangClient= new RedisWangClient(redisWangProperties.getAddress(),redisWangProperties.getPort());
                break;
            case "sentinel":
                redisWangClient = new RedisWangClient(redisWangProperties.getMasterName(),redisWangProperties.getSentinels());
                break;
            case "cluster":
                String clusterHost = redisWangProperties.getClusterHost();
                String[] clusterArray = clusterHost.split(",");
                redisWangClient = new RedisWangClient(new HashSet(Arrays.asList(clusterArray)),15000);
                break;
        }

        return redisWangClient;
    }


}
