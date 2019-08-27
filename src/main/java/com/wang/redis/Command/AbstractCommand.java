package com.wang.redis.Command;

import com.wang.redis.Serializer.KeySerializer;
import com.wang.redis.Serializer.Serializer;
import com.wang.redis.Serializer.StringRedisSerializer;
import com.wang.redis.Serializer.ValueSerializer;
import com.wang.redis.connection.Connection;
import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand<T> extends BaseCommand{

    private Serializer keySerializer = new StringRedisSerializer();

    protected Connection connection;

    public static final byte[] PART = new byte[] { ':' };

    public AbstractCommand(Connection connection){
        this.connection = connection;
    }

    public T execute(Connection connection, Object... arguments) {
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("连接不存在或者已经关闭了!");
        }
        T result = null;
        try {
            //发送命令
            send(connection.getOutputStream(), arguments);
            connection.getOutputStream().flush();
            //接收命令
            result = (T) receive(connection.getInputStream(), arguments);
            connection.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException("执行失败!", e);
        }
        connection.close();
        return result;
    }

    protected void send(OutputStream outputStream, Object... arguments) throws Exception {

    }

    protected String getStatusCodeReply() {
        try {
            connection.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] resp = (byte[])process((RedisInputStream) connection.getInputStream());
        if (null == resp) {
            return null;
        } else {
            return keySerializer.deserialize(resp).toString();
        }
    }

    public static final byte MINUS_BYTE = '-';
    public static final byte COLON_BYTE = ':';
    public static final byte DOLLAR_BYTE = '$';
    public static final byte PLUS_BYTE = '+';

    private Object process(final RedisInputStream is) {
        try {
            byte b = is.readByte();
            if (b == MINUS_BYTE) {
                processError(is);
            } else if (b == ASTERISK_BYTE) {
                return processMultiBulkReply(is);
            } else if (b == COLON_BYTE) {
                return processInteger(is);
            } else if (b == DOLLAR_BYTE) {
                return processBulkReply(is);
            } else if (b == PLUS_BYTE) {
                return processStatusCodeReply(is);
            } else {
                throw new RuntimeException("Unknown reply: " + (char) b);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private void processError(final RedisInputStream is) throws IOException {
        String message = is.readLine();
        throw new RuntimeException(message);
    }

    private List<Object> processMultiBulkReply(final RedisInputStream is) throws IOException {
        int num = Integer.parseInt(is.readLine());
        if (num == -1) {
            return null;
        }
        List<Object> ret = new ArrayList<Object>(num);
        for (int i = 0; i < num; i++) {
            try {
                ret.add(process(is));
            } catch (Exception e) {
                ret.add(e);
            }
        }
        return ret;
    }
    private Long processInteger(final RedisInputStream is) throws IOException {
        String num = is.readLine();
        return Long.valueOf(num);
    }
    private byte[] processBulkReply(final RedisInputStream is) throws IOException {
        int len = Integer.parseInt(is.readLine());
        if (len == -1) {
            return null;
        }
        byte[] read = new byte[len];
        int offset = 0;
        try {
            while (offset < len) {
                offset += is.read(read, offset, (len - offset));
            }
            // read 2 more bytes for the command delimiter
            is.readByte();
            is.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return read;
    }
    private byte[] processStatusCodeReply(final RedisInputStream is) throws IOException {
        return rawString(is.readLine());
    }




    public Boolean set(Object key, Object value) throws IOException {
        RedisOutputStream outputStream = (RedisOutputStream) connection.getOutputStream();
        sendCommand(outputStream,Command.mset, rawKey(1,key), rawValue(value));
        //接收返回的结果
        String s = getStatusCodeReply();
        System.out.println("redis======");
        System.out.println(s);
        return true;
    }


    protected byte[] rawKey(int namespace, Object key) throws UnsupportedEncodingException {
        if (key == null) {
            return null;
        }
        byte[] prefix = rawString(String.valueOf(namespace));
        byte[] byteKey = keySerializer.serialize(key);
        byte[] result = new byte[prefix.length + byteKey.length + 1];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(PART, 0, result, prefix.length, 1);
        System.arraycopy(byteKey, 0, result, prefix.length + 1, byteKey.length);
        return result;
    }

    protected byte[] rawValue(Object value) throws UnsupportedEncodingException {
        if (value == null) {
            throw new IllegalArgumentException("值不能为空哦");
        }
        return keySerializer.serialize(value);
    }



    public static final byte ASTERISK_BYTE = '*';
    private void sendCommand(final RedisOutputStream os, final Command command, final byte[]... args) {
        String commandString = command.name();
        byte[] c = stringToBytes(commandString);
        try {
            os.write(ASTERISK_BYTE);
            os.writeIntCrLf(args.length + 1);
            os.write(DOLLAR_BYTE);
            os.writeIntCrLf(c.length);
            os.write(c);
            os.writeCrLf();
            for (final byte[] arg : args) {
                os.write(DOLLAR_BYTE);
                os.writeIntCrLf(arg.length);
                os.write(arg);
                os.writeCrLf();
            }
        } catch (IOException e) {
            throw new RuntimeException("发送命令失败"+e.getMessage());
        }
    }

    /**
     * @Description 接收信息
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    protected abstract Object receive(InputStream inputStream, Object... arguments) throws Exception;


    public static byte[] stringToBytes(String s){
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
