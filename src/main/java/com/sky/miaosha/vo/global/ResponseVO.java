package com.sky.miaosha.vo.global;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 * jdk: 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseVO<T> {


    private Integer code;

    private String msg;

    private T data;


    public static ResponseVO  success() {
        return success(null);
    };

    public static <T> ResponseVO  success(T data) {
       return ResponseVO.builder().code(0).msg("success").data(data).build();
    };


}
