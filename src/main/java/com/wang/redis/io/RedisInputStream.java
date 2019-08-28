package com.wang.redis.io;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RedisInputStream extends FilterInputStream {
    private static final Logger LOGGER = Logger.getLogger(FilterInputStream.class);

    protected byte[] buf;

    protected int count, limit,size;


    public RedisInputStream(InputStream in) {
        this(in,8192);
    }

    public RedisInputStream(InputStream in,int size){
        super(in);
        this.size = size;
        buf = new byte[size];
    }

    public void clear(){
        buf = new byte[size];
        count = 0;
        limit = 0;
    }

    public byte readByte() throws IOException {
        ensureFill();
        return buf[count++];
    }

    private void fill() throws IOException {
        limit = in.read(buf);
        count = 0;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        ensureFill();

        final int length = Math.min(limit - count, len);
        System.arraycopy(buf, count, b, off, length);
        count += length;
        return length;
    }

    public String readLine() throws IOException {
        final StringBuilder sb = new StringBuilder();
        while (true) {
            ensureFill();

            byte b = buf[count++];
            if (b == '\r') {
                ensureFill(); // Must be one more byte

                byte c = buf[count++];
                if (c == '\n') {
                    break;
                }
                sb.append((char) b);
                sb.append((char) c);
            } else {
                sb.append((char) b);
            }
        }

        final String reply = sb.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read line [" + reply + "]");
        }
        if (reply.length() == 0) {
            throw new IOException("It seems like server has closed the connection.");
        }

        return reply;
    }

    private void ensureFill() throws IOException {
        if (count >= limit) {
            try {
                limit = in.read(buf);
                count = 0;
                if (limit == -1) {
                    throw new IOException("Unexpected end of stream.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException(e);
            }
        }
    }

    private byte[] readLineBytesSlowly() throws IOException {
        ByteArrayOutputStream bout = null;
        while (true) {
            ensureFill();

            byte b = buf[count++];
            if (b == '\r') {
                ensureFill(); // Must be one more byte

                byte c = buf[count++];
                if (c == '\n') {
                    break;
                }

                if (bout == null) {
                    bout = new ByteArrayOutputStream(16);
                }

                bout.write(b);
                bout.write(c);
            } else {
                if (bout == null) {
                    bout = new ByteArrayOutputStream(16);
                }

                bout.write(b);
            }
        }

        return bout == null ? new byte[0] : bout.toByteArray();
    }
    public byte[] readLineBytes() throws IOException {

        ensureFill();

        int pos = count;
        final byte[] buf = this.buf;
        while (true) {
            if (pos == limit) {
                return readLineBytesSlowly();
            }

            if (buf[pos++] == '\r') {
                if (pos == limit) {
                    return readLineBytesSlowly();
                }

                if (buf[pos++] == '\n') {
                    break;
                }
            }
        }

        final int N = (pos - count) - 2;
        final byte[] line = new byte[N];
        System.arraycopy(buf, count, line, 0, N);
        count = pos;
        return line;
    }
}
