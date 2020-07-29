package com.sky.miaosha.advice;

import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.vo.global.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * description:
 * jdk: 1.8
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHander {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVO> catchException(HttpServletRequest request, Exception ex) {
        ResponseVO responseVO = new ResponseVO();
        if (ex instanceof BusinessException) {
            BusinessException ex1 = (BusinessException) ex;
            responseVO.setCode(ex1.getCode());
            responseVO.setMsg(ex1.getMsg());
            log.warn("catchException BusinessException: ex={}", ex.getMessage());
        } else if (ex instanceof ServletRequestBindingException) {
            // MVC参数绑定异常
            ServletRequestBindingException servletRequestBindingException = (ServletRequestBindingException) ex;
            responseVO.setCode(ExceptionEnum.SERVLET_BINDING_EXCEPTION.getCode());
            responseVO.setMsg(ExceptionEnum.SERVLET_BINDING_EXCEPTION.getMsg());
            log.warn("catchException ServletRequestBindingException: ex={}", servletRequestBindingException.getMessage());
        } else {
            responseVO.setCode(ExceptionEnum.UNKNOW_ERROR.getCode());
            responseVO.setMsg(ExceptionEnum.UNKNOW_ERROR.getMsg());
            log.warn("catchException ex={}", ex.getMessage());
        }
        return new ResponseEntity(responseVO, HttpStatus.OK);
    }


}
