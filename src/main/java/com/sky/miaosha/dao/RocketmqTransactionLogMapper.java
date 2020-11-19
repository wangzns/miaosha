package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.ItemStock;
import com.sky.miaosha.dataobject.RocketmqTransactionLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface RocketmqTransactionLogMapper extends Mapper<RocketmqTransactionLog> {

}