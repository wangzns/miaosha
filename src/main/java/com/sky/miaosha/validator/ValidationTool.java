package com.sky.miaosha.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;


/**
 * description:
 * jdk: 1.8
 */
@Component
public class ValidationTool implements InitializingBean {

    private Validator validator;

    /**
     * 校验参数
     * @param bean
     * @return
     */
    public ValidationResult validate(Object bean) {
        ValidationResult result = new ValidationResult();
        Map<String, String> errorMap = result.getErrorMap();
        // 校验结果
        Set<ConstraintViolation<Object>> validateSet = validator.validate(bean);
        if (!CollectionUtils.isEmpty(validateSet)) {
            result.setHasErrors(true);
            validateSet.forEach(va -> {
                String errMsg = va.getMessage();
                String propertyName = va.getPropertyPath().toString();
                errorMap.put(propertyName, errMsg);
            });
        } else {
            result.setHasErrors(false);
        }
        return result;
    }

    /**
     * 在该bean实例化完成之后调用以下方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
