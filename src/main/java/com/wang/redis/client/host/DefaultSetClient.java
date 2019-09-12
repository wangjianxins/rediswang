package com.wang.redis.client.host;

import com.wang.redis.Command.Command;
import com.wang.redis.result.IntResult;
import com.wang.redis.result.ObjectResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description set客户端
 * @author Jianxin Wang
 * @date 2019-09-09
 */
public class DefaultSetClient implements SetClient  {

    private String currentkey;
    private RedisWangClient client;

    public DefaultSetClient(String key,RedisWangClient client){
        this.currentkey = key;
        this.client = client;
    }

    @Override
    public int sadd(Object... value) {
        return client.doExecute(Command.sadd, IntResult.class,currentkey,value);
    }

    @Override
    public int srem(Object... value) {
        return client.doExecute(Command.srem, IntResult.class,getCurrentkey(),value);
    }

    @Override
    public int scount() {
        return client.doExecute(Command.scard, IntResult.class,getCurrentkey());
    }

    @Override
    public int scontains(Object item) {
        return client.doExecute(Command.sismember, IntResult.class,getCurrentkey(),item);
    }

    @Override
    public Set srandmerber(int count) {
        List list = client.doExecute(Command.srandmerber, ObjectResult.class,getCurrentkey(),count);
        return new HashSet(list);
    }

    @Override
    public Object spop() {
        return client.doExecute(Command.spop, ObjectResult.class,getCurrentkey());
    }

    @Override
    public Set getall() {
        List list = client.doExecute(Command.smembers, ObjectResult.class,getCurrentkey());
        return new HashSet<>(list);
    }

    @Override
    public Set sunion(String key2) {
        List list = client.doExecute(Command.sunion, ObjectResult.class,getCurrentkey(),key2);
        return new HashSet(list);
    }

    @Override
    public Set sdiff(String key2) {
        List list = client.doExecute(Command.sdiff, ObjectResult.class,getCurrentkey(),key2);
        return new HashSet(list);
    }

    public String getCurrentkey() {
        return currentkey;
    }

    public void setCurrentkey(String currentkey) {
        this.currentkey = currentkey;
    }
}
