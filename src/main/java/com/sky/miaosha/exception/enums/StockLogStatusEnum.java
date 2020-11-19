package com.sky.miaosha.exception.enums;

import lombok.Getter;

/**
 * @author : wang zns
 * @date : 2020-11-19
 */
@Getter
public enum  StockLogStatusEnum {



    INIT(1),
    COMMIT(2),
    ROLLBACK(3),

    ;
    Integer code;
    StockLogStatusEnum(Integer code) {
        this.code = code;
    }

}
