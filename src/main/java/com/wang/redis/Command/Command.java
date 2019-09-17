package com.wang.redis.Command;

/**
 * @Description 操作类型枚举
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public enum Command {

    set,

    setex,

    del,

    get,

    incr,

    mget,

    mset,

    expire,

    pfadd,

    pfcount,

    lindex,

    lrange,

    lset,

    lpush,

    rpush,

    lpop,

    rpop,

    /**
     * hash
     */
    hset,
    hmset,
    hmget,
    hgetall,
    hget,
    hdel,
    hkeys,
    hvals,

    /**
     * set
     */
    sadd,
    srem,
    //个数
    scard,
    //是否存在
    sismember,
    //弹出一个
    spop,
    //随机选择一个，不会删除
    srandmerber,
    //getall
    smembers,
    //求俩个set的交集
    sunion,
    //求俩个set的差
    sdiff,

    /**
     * zset
     */
    zadd,
    //个数
    zcard,
    //获得某个成员的值
    zscore,
    zrem,
    zrank,
    zrevrank,
    //zincrby key 1 filed自增多少
    zincrby,
    //正序排列，zrange zset 0 1
    zrange,
    //倒叙排列
    zrevrange,
    //根据score返回范围，给定分值范围，zrangebyscore zset 0 14
    zrangebyscore,
    //根据score倒序排列，给定分值范围
    zrevrangebyscore,
    //指定范围计算count,zcount key min max
    zcount,


    /**
     * bitmaps
     */
    //setbit key offset value
    setbit,
    //getbit key offset
    getbit,



    //
    sentinel,



    //
    subscribe,


    //集群
    cluster,

}
