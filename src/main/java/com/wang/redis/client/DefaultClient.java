package com.wang.redis.client;

import java.util.List;
import java.util.Map;

public abstract class DefaultClient implements BaseClient {

    @Override
    public abstract int del(String... key);

    @Override
    public abstract boolean set(String key, String value, long expires);

    @Override
    public abstract String get(String key);

    @Override
    public boolean mset(String[] keys, Object... values) {
        return false;
    }

    @Override
    public List<String> mget(String... keys) {
        return null;
    }

    @Override
    public int incr(String key) {
        return 0;
    }

    @Override
    public boolean expire(String key, long seconds) {
        return false;
    }

    @Override
    public boolean tryLock(String key, long expires) {
        return false;
    }

    @Override
    public int pfadd(String key, Object... param) {
        return 0;
    }

    @Override
    public int pfcount(String key) {
        return 0;
    }

    @Override
    public Boolean setListIndex(String key, int index, Object value) {
        return null;
    }

    @Override
    public Object getListIndex(String key, int index) {
        return null;
    }

    @Override
    public List getRangeList(String key, int start, int end) {
        return null;
    }

    @Override
    public int leftPush(String key, Object value) {
        return 0;
    }

    @Override
    public int rightPush(String key, Object value) {
        return 0;
    }

    @Override
    public Object leftPop(String key, Boolean blocking) {
        return null;
    }

    @Override
    public Object rightPop(String key, Boolean blocking) {
        return null;
    }

    @Override
    public int hset(String key, String filed, Object o) {
        return 0;
    }

    @Override
    public Boolean hsetObject(String key, Object o) {
        return null;
    }

    @Override
    public Object hget(String key, String filed) {
        return null;
    }

    @Override
    public int hdel(String key, String filed) {
        return 0;
    }

    @Override
    public List<Object> hkeys(String key) {
        return null;
    }

    @Override
    public List<Object> hvals(String key) {
        return null;
    }

    @Override
    public Map<String, Object> hgetall(String key) {
        return null;
    }
}
