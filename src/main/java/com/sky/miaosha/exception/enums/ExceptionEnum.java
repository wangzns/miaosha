package com.sky.miaosha.exception.enums;

import com.sky.miaosha.exception.CommonException;
import lombok.Getter;

/**
 * description:
 * jdk: 1.8
 */
public enum ExceptionEnum implements CommonException {

    PARAM_ERROR(111111, "参数错误"),
    UNKNOW_ERROR(-1, "未知错误"),
    SERVLET_BINDING_EXCEPTION(555555, "URL参数绑定错误"),

    URL_NOT_FOUND_EXCEPTION(555500, "未找到页面"),

    // 100000开头用户相关错误

    USER_NOT_EXIST(100001, "用户不存在"),
    USER_PASSWORD_NOT_EXIST(100002, "用户密码不存在"),
    USER_LOGIN_FAIL(100003, "用户登录失败"),
    PASSWORD_NOT_MATCH(100004, "密码错误"),
    USER_NOT_LOGIN(100005, "用户未登录"),

    // 200000开头商品相关错误
    ITEM_NOT_EXIST(200001, "商品不存在"),
    ITEM_STOCK_NOT_EXIST(200002, "商品库存不存在"),
    ITEM_STOCK_NOT_ENOUGH(200003, "库存不足"),

    // 300000开头活动相关错误
    PROMO_NOT_EXIST(300001, "活动不存在"),

    // mq消息
    MQ_SEND_FAIL(700001, "异步消息投递失败"),







    ;


    ;


    private Integer code;

    private String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public void overrideMsg(String newMsg) {
        this.msg = newMsg;
    }
}
