package com.wang.redis.Command;

import com.wang.redis.Serializer.Serializer;
import com.wang.redis.Serializer.StringRedisSerializer;
import com.wang.redis.connection.Connection;
import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class RedisWangClinet extends AbstractCommand {
    protected final Logger LOGGER = Logger.getLogger(getClass());

    Serializer<String> s = new StringRedisSerializer();

    public RedisWangClinet(Connection connection) {
        super(connection);
    }

    @Override
    protected Object receive(InputStream inputStream, Object... arguments) throws Exception {
        RedisInputStream in = (RedisInputStream) inputStream;
        String response = in.readLine();
        if (isOk(response)) {
            return true;
        } else {
            throw new RuntimeException(extractResult(response));
        }
    }

    private static final String trueResultPrefix = "+";
    public static boolean isOk(String response) {
        return response != null && response.startsWith(trueResultPrefix);
    }

    public static boolean isStringLengthResultOk(String response) {
        return response != null && response.startsWith(stringLengthResultPrefix);
    }

    public static String extractResult(String response) {
        return (response == null || response.length() == 0) ? null : response.substring(1);
    }

    private static final String stringLengthResultPrefix = "$";
    private static final String COMMAND_SEPARATOR = "_";
    private static final String SPACE = " ";

    protected void send(OutputStream outputStream, Command command,Object... arguments) throws Exception {
        String commandString = command.name();
        if (command.name().indexOf(COMMAND_SEPARATOR) > 0) {
            commandString = command.name().replace(COMMAND_SEPARATOR, SPACE);
        }
        byte[][] argumentBytes = new byte[arguments.length][];
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof byte[]) {
                argumentBytes[i] = (byte[]) arguments[i];

            } else if (List.class.isAssignableFrom(arguments[i].getClass())) {
                List<?> list = (List<?>) arguments[i];
                byte[][] extendArgumentBytes = new byte[arguments.length + list.size() - 1][];
                System.arraycopy(argumentBytes, 0, extendArgumentBytes, 0, i);
                for (int j = 0; j < list.size(); j++) {
                    extendArgumentBytes[i++] = stringToBytes(list.get(j).toString());
                }
                argumentBytes = extendArgumentBytes;

            } else if (arguments[i].getClass().isArray()) {
                Object[] array = (Object[]) arguments[i];
                byte[][] extendArgumentBytes = new byte[arguments.length + array.length - 1][];
                System.arraycopy(argumentBytes, 0, extendArgumentBytes, 0, i);
                for (int j = 0; j < array.length; j++) {
                    extendArgumentBytes[i++] = stringToBytes(array[j].toString());
                }
                argumentBytes = extendArgumentBytes;

            } else {
                argumentBytes[i] = stringToBytes(arguments[i].toString());
            }
        }
        sendCommand((RedisOutputStream) outputStream, stringToBytes(commandString), argumentBytes);
    }


    private static final byte arrayLengthResultPrefixByte = '*';
    private static final byte stringLengthResultPrefixByte = '$';


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


    @Override
    public Boolean set(Object key, Object value) throws IOException {
        String keys = (String) key;
        Object[] arguments = new Object[2];
        arguments[0] = keys;
        arguments[1] = value;

        Boolean result = true;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(getClass() + " send command : " + Command.mset + " outputstream : " + connection.getOutputStream());
            }
            send(connection.getOutputStream(), Command.mset, arguments);
            connection.getOutputStream().flush();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(getClass() + " receive data , command : " + Command.mset + " inputstream : " + connection.getInputStream());
            }

            result = (Boolean) receive(connection.getInputStream(), Command.mset, arguments);
            connection.getInputStream().clear();
        } catch (Exception e) {
            throw new RuntimeException("command execute failed!", e);
        }
        connection.close();
        return result;
    }
}
