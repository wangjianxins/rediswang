package com.wang.redis.client.host;


import java.util.Map;
import java.util.Set;

public interface ZsetClient {

    int zadd(Double score,Object member);

    int zadd(Map<String,Double> map);

    int zcount();

    double zgetValueScore(Object member);

    int zrem(Object... member);

    //计算排名
    int zrank(Object member);

    //计算排名
    int zrevrank(Object member);

    //增加分数
    double incr(double incrscore,Object member);

    Set zrange(int start, int end,Boolean withScore);

    Set zrevrange(int start, int end,Boolean withScore);

    Set zrangebyscore(double start,double end,Boolean withScore);

    Set zrevrangebyscore(double start,double end,Boolean withScore);

    int zcountbyscore(double start,double end);

}
