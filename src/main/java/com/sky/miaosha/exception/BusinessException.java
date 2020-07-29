package com.sky.miaosha.exception;

import com.sky.miaosha.exception.enums.ExceptionEnum;
import org.omg.SendingContext.RunTime;

/**
 * description:
 * jdk: 1.8
 */
public class BusinessException extends RuntimeException implements CommonException{

    private CommonException commonException;


    public BusinessException(CommonException exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.commonException = exceptionEnum;
    }

    public BusinessException(CommonException exceptionEnum, String overrideMsg) {
        super(overrideMsg);
        this.commonException = exceptionEnum;
        this.commonException.overrideMsg(overrideMsg);
    }


    @Override
    public Integer getCode() {
        return commonException.getCode();
    }

    @Override
    public String getMsg() {
        return commonException.getMsg();
    }

    @Override
    public void overrideMsg(String newMsg) { }
}
