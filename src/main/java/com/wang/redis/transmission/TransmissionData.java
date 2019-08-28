package com.wang.redis.transmission;

import com.wang.redis.io.RedisOutputStream;

import java.io.IOException;

/**
 * @Description 此类用于数据的传输
 *
 * 此类借鉴jedis源代码。。。
 *
 * @author Jianxin Wang
 * @date 2019-08-28
 */
public class TransmissionData {


    public static final String trueResultPrefix = "+";
    public static final String COMMAND_SEPARATOR = "_";
    public static final String SPACE = " ";
    public static final byte arrayLengthResultPrefixByte = '*';
    public static final byte stringLengthResultPrefixByte = '$';


    public static void sendCommand(RedisOutputStream outputStream, final byte[] command, final byte[]... args) {
        try {
            outputStream.write(arrayLengthResultPrefixByte);
            outputStream.writeIntCrLf(args.length + 1);
            outputStream.write(stringLengthResultPrefixByte);
            outputStream.writeIntCrLf(command.length);
            outputStream.write(command);
            outputStream.writeCrLf();
            for (final byte[] arg : args) {
                outputStream.write(stringLengthResultPrefixByte);
                outputStream.writeIntCrLf(arg.length);
                outputStream.write(arg);
                outputStream.writeCrLf();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
