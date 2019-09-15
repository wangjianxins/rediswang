package com.wang.redis.client.sentinel;

import com.wang.redis.Command.Command;
import com.wang.redis.Command.CommandService;
import com.wang.redis.client.host.DefaultExecute;
import com.wang.redis.connection.impl.SentinelPoolImpl;
import com.wang.redis.result.ObjectResult;
import java.util.List;

public class SimpleSentinelClient extends DefaultExecute {


    public SimpleSentinelClient(String address,int port){
        super(new SentinelPoolImpl(address,port));
    }

    //===========================哨兵相关

    /**
     * 这个api会获取真正的master地址，包括在故障转移过程中
     */
    public List<String> getSentinelMasterByName(String masterName){
        return (List<String>) doExecute(Command.sentinel, ObjectResult.class, CommandService.GET_MASTER,masterName);
    }
}
