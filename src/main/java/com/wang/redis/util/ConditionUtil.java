package com.wang.redis.util;

import com.wang.redis.transmission.TransmissionData;

import java.util.Arrays;

/**
 * @Description 用于条件判断
 * @author Jianxin Wang
 * @date 2019-08-28
 */
public class ConditionUtil {


    public static boolean isOk(String response) {
        return response != null && response.startsWith(TransmissionData.trueResultPrefix);
    }

    public static String extractResult(String response) {
        return (response == null || response.length() == 0) ? null : response.substring(1);
    }
}
