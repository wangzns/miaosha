package com.sky.miaosha.utils;

import java.util.Random;

/**
 * description:
 * jdk: 1.8
 */
public class CommonUtil {


    /**
     * 生成随机验证码
     * @return
     */
    public static String randomOtp() {
        Random random = new Random();
        int opt = random.nextInt(90000);
        opt += 10000;
        return String.valueOf(opt);
    }

}
