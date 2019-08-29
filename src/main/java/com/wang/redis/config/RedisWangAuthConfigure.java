package com.wang.redis.config;


//自动装配类

import com.wang.redis.client.RedisWangClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RedisWangClient.class)
@EnableConfigurationProperties(RedisWangProperties.class)
public class RedisWangAuthConfigure {

    @Autowired
    private RedisWangProperties redisWangProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "redis.wang",value = "enabled",havingValue = "true")
    public RedisWangClient getClient(){
        return new RedisWangClient(redisWangProperties);
    }
}
