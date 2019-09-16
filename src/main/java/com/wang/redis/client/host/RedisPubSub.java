package com.wang.redis.client.host;

import com.wang.redis.Command.Command;
import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.Serializer.StringRedisSerializer;
import com.wang.redis.connection.Connection;
import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;
import com.wang.redis.result.ObjectResult;
import com.wang.redis.transmission.TransmissionData;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @Description pubsub
 * @author Jianxin Wang
 * @date 2019-09-15
 */
public abstract class RedisPubSub{
    private static final Logger logger = Logger.getLogger(RedisPubSub.class);

    private int subscribedChannels = 0;

    public void subscribe(Connection connection, Command command,String channel){
        this.doExecute(connection,command,channel);
    }

    public void doExecute(Connection connection, Command command, Object... params) {
        try {
            connection.setTimeoutInfinite();
            RedisOutputStream o  = connection.getOutputStream();
            TransmissionData.sendCommand(o, StringRedisSerializer.serialize(command.name()), StringRedisSerializer.serialize(params[0].toString()));
            o.flush();
            process(connection.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("command 执行错误!", e);
        }
    }

    protected Object process(RedisInputStream inputStream) throws Exception {
        do {
            List<Object> reply = (List<Object>) ObjectResult.process(inputStream);
            reply.set(0,StringRedisSerializer.serialize((String) reply.get(0)));
            reply.set(1,StringRedisSerializer.serialize((String) reply.get(1)));
            if(reply.get(2) instanceof String){
                reply.set(2,StringRedisSerializer.serialize(reply.get(2).toString()));
            }

            final Object firstObj = reply.get(0);
            final byte[] resp = (byte[]) firstObj;
            if (Arrays.equals(StringRedisSerializer.serialize("subscribe"), resp)) {
                subscribedChannels = ((Long) reply.get(2)).intValue();
                final byte[] bchannel = (byte[]) reply.get(1);
                final String strchannel = (bchannel == null) ? null : StringRedisSerializer.deserialize(bchannel);
                onSubscribe(strchannel, subscribedChannels);
            } else if (Arrays.equals(StringRedisSerializer.serialize("unsubscribe"), resp)) {
                subscribedChannels = ((Long) reply.get(2)).intValue();
                final byte[] bchannel = (byte[]) reply.get(1);
                final String strchannel = (bchannel == null) ? null : StringRedisSerializer.deserialize(bchannel);
                onUnsubscribe(strchannel, subscribedChannels);
            } else if (Arrays.equals(StringRedisSerializer.serialize("message"), resp)) {
                final byte[] bchannel = (byte[]) reply.get(1);
                final byte[] bmesg = (byte[]) reply.get(2);
                final String strchannel = (bchannel == null) ? null : StringRedisSerializer.deserialize(bchannel);
                final String strmesg = (bmesg == null) ? null : StringRedisSerializer.deserialize(bmesg);
                onMessage(strchannel, strmesg);
            } else if (Arrays.equals(StringRedisSerializer.serialize("pmessage"), resp)) {
                final byte[] bpattern = (byte[]) reply.get(1);
                final byte[] bchannel = (byte[]) reply.get(2);
                final byte[] bmesg = (byte[]) reply.get(3);
                final String strpattern = (bpattern == null) ? null : StringRedisSerializer.deserialize(bpattern);
                final String strchannel = (bchannel == null) ? null : StringRedisSerializer.deserialize(bchannel);
                final String strmesg = (bmesg == null) ? null : StringRedisSerializer.deserialize(bmesg);
                onPMessage(strpattern, strchannel, strmesg);
            } else if (Arrays.equals(StringRedisSerializer.serialize("psubscribe"), resp)) {
                subscribedChannels = ((Long) reply.get(2)).intValue();
                final byte[] bpattern = (byte[]) reply.get(1);
                final String strpattern = (bpattern == null) ? null : StringRedisSerializer.deserialize(bpattern);
                onPSubscribe(strpattern, subscribedChannels);
            } else if (Arrays.equals(StringRedisSerializer.serialize("pubsubscribe"), resp)) {
                subscribedChannels = ((Long) reply.get(2)).intValue();
                final byte[] bpattern = (byte[]) reply.get(1);
                final String strpattern = (bpattern == null) ? null : StringRedisSerializer.deserialize(bpattern);
                onPUnsubscribe(strpattern, subscribedChannels);
            } else if (Arrays.equals(StringRedisSerializer.serialize("pong"), resp)) {
                final byte[] bpattern = (byte[]) reply.get(1);
                final String strpattern = (bpattern == null) ? null : StringRedisSerializer.deserialize(bpattern);
                onPong(strpattern);
            } else {
                throw new RedisWangException("不知道的pubsb返回类型: " + firstObj);
            }
            logger.info("subscribedChannels====:"+subscribedChannels);
        } while (subscribedChannels > 0);

        return null;
    }


    public abstract void onMessage(String channel, String message) ;

    public void onPMessage(String pattern, String channel, String message) {
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info("成功订阅"+subscribedChannels+"个频道："+channel);
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
    }

    public void onPUnsubscribe(String pattern, int subscribedChannels) {
    }

    public void onPSubscribe(String pattern, int subscribedChannels) {
    }

    public void onPong(String pattern) {

    }
}
