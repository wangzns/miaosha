package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.ItemStock;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ItemStockMapper extends Mapper<ItemStock> {

    ItemStock selectByItemId(Integer itemId);

    int
    decreaseStock(@Param("itemId") Integer itemId, @Param("amount") Integer amount);
}