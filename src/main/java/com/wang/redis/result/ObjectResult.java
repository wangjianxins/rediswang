package com.wang.redis.result;

import com.wang.redis.Command.Command;
import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.client.AbstractExecute;
import com.wang.redis.io.RedisInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description object
 * @author Jianxin Wang
 * @date 2019-09-02
 */
public class ObjectResult extends AbstractExecute<List> {

    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';
    public static final byte PLUS_BYTE = '+';
    public static final byte MINUS_BYTE = '-';
    public static final byte COLON_BYTE = ':';

    @Override
    protected Object receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        return this.process(inputStream);
    }

    public Object process(RedisInputStream inputStream) throws Exception{
        //需要判断不同的情况
        final byte b = inputStream.readByte();
        if (b == PLUS_BYTE) {
            return processStatusCodeReply(inputStream);
        } else if (b == DOLLAR_BYTE) {
            return processBulkReply(inputStream);
        } else if (b == ASTERISK_BYTE) {
            return processMultiBulkReply(inputStream);
        } else if (b == COLON_BYTE) {
            return processInteger(inputStream);
        } else if (b == MINUS_BYTE) {
            throw new RedisWangException("redis指令错误,或者指令和数据不匹配");
        } else {
            throw new RedisWangException("接收错误数据");
        }
    }

    public static byte[] processStatusCodeReply(RedisInputStream in){
        try {
            return in.readLineBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object processBulkReply(RedisInputStream in) throws Exception{
        int len = (int) in.readLongCrLf();
        if (len == -1) {
            return null;
        }

        final byte[] read = new byte[len];
        int offset = 0;
        while (offset < len) {
            final int size = in.read(read, offset, (len - offset));
            if (size == -1) throw new RedisWangException("接收数据连接错误");
            offset += size;
        }

        // read 2 more bytes for the command delimiter
        in.readByte();
        in.readByte();
        return fasterSerializer.deserialize(read);
    }

    private List<Object> processMultiBulkReply(final RedisInputStream in) throws IOException {
        String result = in.readLine();
        int len = Integer.valueOf(result.replace("*","").trim());

        if (len == -1) {
            return null;
        }
        final List<Object> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            try {
                //再去判断他item什么类型，一般不会是list的，在导致递归一次
                ret.add(process(in));
            } catch (Exception e) {
                ret.add(e);
            }
        }
        return ret;
    }

    public static Long processInteger(RedisInputStream inputStream) throws IOException {
        String result = inputStream.readLine();
        if(result.contains(":")){
            return Long.valueOf(result.replace(":",""));
        }
        return 0l;
    }


}
