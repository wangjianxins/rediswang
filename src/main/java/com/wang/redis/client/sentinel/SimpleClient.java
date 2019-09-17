package com.wang.redis.client.sentinel;

import com.wang.redis.Command.Command;
import com.wang.redis.Serializer.StringRedisSerializer;
import com.wang.redis.client.host.DefaultExecute;
import com.wang.redis.connection.impl.SentinelPoolImpl;
import com.wang.redis.result.ObjectResult;
import java.util.List;

/**
 * @Description 有关哨兵模式下的命令
 * @author Jianxin Wang
 * @date 2019-09-15
 */
public class SimpleClient extends DefaultExecute {

    public static final String GET_MASTER = "get-master-addr-by-name";


    public SimpleClient(String address, int port){
        super(new SentinelPoolImpl(address,port));
    }

    /**
     * 这个api会获取真正的master地址，包括在故障转移过程中
     */
    public List<Object> getSentinelMasterByName(String masterName){
        return (List<Object>) doExecute(Command.sentinel, ObjectResult.class,
                StringRedisSerializer.serialize(GET_MASTER),
                StringRedisSerializer.serialize(masterName));
    }
}
