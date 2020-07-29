package com.sky.miaosha.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 * jdk: 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /**
     * 校验是否有错误
     */
    private Boolean hasErrors;

    /**
     * 错误信息
     * key   -> 属性
     * value -> 描述
     */
    private Map<String, String> errorMap = new HashMap<>();


    public Boolean hasErrors() {
        return this.hasErrors;
    }


    public String getErrorDetail() {
        Object[] faultDetailArray = errorMap.values().toArray();
        return StringUtils.join(faultDetailArray, ",");
    }




}
