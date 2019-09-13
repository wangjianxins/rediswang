package com.wang.redis.client.host;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wang.redis.Command.Command;
import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.result.IntResult;
import com.wang.redis.result.ObjectResult;
import com.wang.redis.transmission.Tuple;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Description zsete的默认实现类
 * @author Jianxin Wang
 * @date 2019-09-12
 */
public class DefaultZsetClient implements ZsetClient {

    public static final byte[] POSITIVE_INFINITY_BYTES = "+inf".getBytes();
    public static final byte[] NEGATIVE_INFINITY_BYTES = "-inf".getBytes();

    private static final String withscores = "withscores";
    private RedisWangClient redisWangClient;
    private String currentkey;

    public DefaultZsetClient(String key,RedisWangClient redisWangClient){
        this.currentkey = key;
        this.redisWangClient = redisWangClient;
    }
    public byte[] getCurrentkey() {
        try {
            return currentkey.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RedisWangException("get key byte 错误");
        }
    }

    @Override
    public int zadd(Double score,Object member) {
       return redisWangClient.doExecute(Command.zadd, IntResult.class,getCurrentkey(),score,member);
    }

    @Override
    public int zadd(Map<String, Double> map) {
        HashMap<byte[], Double> binaryScoreMembers = new HashMap<byte[], Double>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            Object s = entry.getValue();
            Double v;
            v = Double.valueOf(s.toString());
            binaryScoreMembers.put(toByteArray(entry.getKey()), v);
        }
        ArrayList<byte[]> args = new ArrayList<>(map.size() * 2 + 1);
        try {
            args.add(getCurrentkey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        args.addAll(convertScoreMembersToByteArrays(binaryScoreMembers));

        byte[][] argsArray = new byte[args.size()][];
        args.toArray(argsArray);

        return redisWangClient.doExecute(Command.zadd, IntResult.class,argsArray);
    }

    private ArrayList<byte[]> convertScoreMembersToByteArrays(final Map<byte[], Double> scoreMembers) {
        ArrayList<byte[]> args = new ArrayList<>(scoreMembers.size() * 2);

        for (Map.Entry<byte[], Double> entry : scoreMembers.entrySet()) {
            args.add(toByteArray(entry.getValue()));
            args.add(entry.getKey());
        }

        return args;
    }

    public static final byte[] toByteArray(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return POSITIVE_INFINITY_BYTES;
        } else if (value == Double.NEGATIVE_INFINITY) {
            return NEGATIVE_INFINITY_BYTES;
        } else {
            try {
                return String.valueOf(value).getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] toByteArray(final String key){
        return JSON.toJSONBytes(key, new SerializerFeature[]{SerializerFeature.WriteClassName});
    }

    @Override
    public int zcount() {
        return redisWangClient.doExecute(Command.zcard, IntResult.class,getCurrentkey());
    }

    @Override
    public double zgetValueScore(Object member) {
        return redisWangClient.doExecute(Command.zscore, IntResult.class,getCurrentkey());
    }

    @Override
    public int zrem(Object... member) {
        return redisWangClient.doExecute(Command.zrem, IntResult.class,getCurrentkey(),member);
    }

    @Override
    public int zrank(Object member) {
        return redisWangClient.doExecute(Command.zrank, IntResult.class,getCurrentkey(),member);
    }



    @Override
    public int zrevrank(Object member) {
        return redisWangClient.doExecute(Command.zrevrank, IntResult.class,getCurrentkey(),member);
    }

    @Override
    public double incr(double incrscore, Object member) {
        return redisWangClient.doExecute(Command.zincrby, IntResult.class,getCurrentkey(),incrscore,member);
    }

    @Override
    public Set zrange(int start, int end, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrange, ObjectResult.class,getCurrentkey(),toByteArray(start),toByteArray(end),withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrange, ObjectResult.class,getCurrentkey(),toByteArray(start),toByteArray(end));

        return new HashSet(list);
    }

    @Override
    public Set zrevrange(int start, int end, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrevrange, ObjectResult.class,getCurrentkey(),toByteArray(start),toByteArray(end),withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrevrange, ObjectResult.class,getCurrentkey(),toByteArray(start),toByteArray(end));

        return new HashSet(list);
    }

    @Override
    public Set zrangebyscore(final double min, final double max, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrangebyscore, ObjectResult.class,getCurrentkey(),toByteArray(min),toByteArray(max),withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrangebyscore, ObjectResult.class,getCurrentkey(),toByteArray(min),toByteArray(max));

        return new HashSet(list);
    }

    @Override
    public Set zrevrangebyscore(double max, double min, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrevrangebyscore, ObjectResult.class,getCurrentkey(),toByteArray(max),toByteArray(min),withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrevrangebyscore, ObjectResult.class,getCurrentkey(),toByteArray(max),toByteArray(min));

        return new HashSet(list);
    }

    @Override
    public int zcountbyscore(double start, double end) {
        return redisWangClient.doExecute(Command.zincrby, IntResult.class,getCurrentkey(),toByteArray(start),toByteArray(end));
    }

    public Set revset(List list){
        Set<Tuple> set = new LinkedHashSet<>(list.size() / 2, 1.0f);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            set.add(new Tuple((String)iterator.next(), (Double)iterator.next()));
        }
        return set;
    }
}
