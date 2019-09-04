package com.wang.redis.result;

import com.wang.redis.Command.Command;
import com.wang.redis.client.AbstractExecute;
import com.wang.redis.io.RedisInputStream;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description 只返回list
 * @author Jianxin Wang
 * @date 2019-09-02
 */
public class ListResult extends AbstractExecute<List> {

    @Override
    protected List receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        String result = inputStream.readLine();

        if(result.contains("*")){
            int len = Integer.valueOf(result.replace("*","").trim());
            List list = new ArrayList(len);

            for(int i = 0; i< len; i++){
                if(isResult(inputStream.readLine())){
                    list.add(inputStream.readLine());
                }
            }
            return list;
        }
        return null;
    }

    private Boolean isResult(String res){
        return res.contains("$") && null != res;
    }
}
