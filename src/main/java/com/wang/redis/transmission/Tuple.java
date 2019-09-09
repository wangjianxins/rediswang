package com.wang.redis.transmission;

import com.wang.redis.Exception.RedisWangException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Objects;

public class Tuple implements Comparable<Tuple> {
    private String element;
    private Double score;

    public Tuple(String element, Double score) {
        super();
        this.element = element;
        this.score = score;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        if (null != element) {
            for (final byte b : element.getBytes()) {
                result = prime * result + b;
            }
        }
        long temp = Double.doubleToLongBits(score);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Tuple)) return false;

        Tuple other = (Tuple) obj;
        if (!Arrays.equals(element.getBytes(), other.element.getBytes())) return false;
        return Objects.equals(score, other.score);
    }

    @Override
    public int compareTo(Tuple other) {
        return compare(this, other);
    }

    public static int compare(Tuple t1, Tuple t2) {
        int compScore = Double.compare(t1.score, t2.score);
        if(compScore != 0) return compScore;

        return compare(t1.element.getBytes(), t2.element.getBytes());
    }

    public String getElement() {
        if (null != element) {
            return encode(element.getBytes());
        } else {
            return null;
        }
    }

    public byte[] getBinaryElement() {
        return element.getBytes();
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return '[' + element + ',' + score + ']';
    }

    public byte[] getbyte(final String str){
        try {
            return str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encode(final String str) {
        try {
            if (str == null) {
            }
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RedisWangException("--");
        }
    }

    public static String encode(final byte[] data) {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RedisWangException("--");
        }
    }


    public static int compare(final byte[] val1, final byte[] val2) {
        int len1 = val1.length;
        int len2 = val2.length;
        int lmin = Math.min(len1, len2);

        for (int i = 0; i < lmin; i++) {
            byte b1 = val1[i];
            byte b2 = val2[i];
            if(b1 < b2) return -1;
            if(b1 > b2) return 1;
        }

        if(len1 < len2) return -1;
        if(len1 > len2) return 1;
        return 0;
    }
}
