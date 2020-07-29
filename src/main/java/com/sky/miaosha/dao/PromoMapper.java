package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.Promo;
import tk.mybatis.mapper.common.Mapper;

public interface PromoMapper extends Mapper<Promo> {

    Promo selectByItemId(Integer itemId);


}