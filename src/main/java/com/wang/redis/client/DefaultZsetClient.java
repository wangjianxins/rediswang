package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.result.IntResult;
import com.wang.redis.result.ObjectResult;
import com.wang.redis.transmission.Tuple;

import java.io.UnsupportedEncodingException;
import java.util.*;

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
    public String getCurrentkey() {
        return currentkey;
    }

    @Override
    public int zadd(Double score,Object member) {
       return redisWangClient.doExecute(Command.zadd, IntResult.class,getCurrentkey(),score,member);
    }

    @Override
    public int zadd(Map<String, Double> map) {
        HashMap<byte[], Double> binaryScoreMembers = new HashMap<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            try {
                binaryScoreMembers.put(entry.getKey().getBytes("utf-8"), entry.getValue());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        ArrayList<byte[]> args = new ArrayList<>(map.size() * 2 + 1);
        try {
            args.add(getCurrentkey().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
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
        return redisWangClient.doExecute(Command.zrange, IntResult.class,getCurrentkey(),member);
    }

    @Override
    public int zrevrank(Object member) {
        return redisWangClient.doExecute(Command.zrevrange, IntResult.class,getCurrentkey(),member);
    }

    @Override
    public double incr(double incrscore, Object member) {
        return redisWangClient.doExecute(Command.zincrby, IntResult.class,getCurrentkey(),incrscore,member);
    }

    @Override
    public Set zrange(int start, int end, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrange, ObjectResult.class,getCurrentkey(),start,end,withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrange, ObjectResult.class,getCurrentkey(),start,end);

        return new HashSet(list);
    }

    @Override
    public Set zrevrange(int start, int end, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrevrange, ObjectResult.class,getCurrentkey(),start,end,withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrevrange, ObjectResult.class,getCurrentkey(),start,end);

        return new HashSet(list);
    }

    @Override
    public Set zrangebyscore(double start, double end, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrangebyscore, ObjectResult.class,getCurrentkey(),start,end,withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrangebyscore, ObjectResult.class,getCurrentkey(),start,end);

        return new HashSet(list);
    }

    @Override
    public Set zrevrangebyscore(double start, double end, Boolean withScore) {
        List list;
        if(withScore){
            list = redisWangClient.doExecute(Command.zrevrangebyscore, ObjectResult.class,getCurrentkey(),start,end,withscores);
            return revset(list);
        }
        list = redisWangClient.doExecute(Command.zrevrangebyscore, ObjectResult.class,getCurrentkey(),start,end);

        return new HashSet(list);
    }

    @Override
    public int zcountbyscore(double start, double end) {
        return redisWangClient.doExecute(Command.zincrby, IntResult.class,getCurrentkey(),start,end);
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
