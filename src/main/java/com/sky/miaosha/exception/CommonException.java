package com.sky.miaosha.exception;

/**
 * @author : wang zns
 * @date : 2019-12-11
 */
public interface CommonException {

    Integer getCode();

    String getMsg();

    void overrideMsg(String newMsg);




}
