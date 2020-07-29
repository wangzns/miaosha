package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.Item;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ItemMapper extends Mapper<Item> {

    List<Item> listItem();

    int increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);

}