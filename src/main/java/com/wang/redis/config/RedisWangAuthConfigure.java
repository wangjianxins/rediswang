package com.wang.redis.config;
import com.wang.redis.client.host.RedisWangClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
        if(null == redisWangProperties.getSentinels() || redisWangProperties.getSentinels().length() == 0){
            logger.info("安装普通模式");
            return new RedisWangClient(redisWangProperties.getAddress(),redisWangProperties.getPort());
        }else{
            //哨兵模式
            logger.info("安装哨兵模式");
            return new RedisWangClient(redisWangProperties.getMasterName(),redisWangProperties.getSentinels());
        }
    }




}
