package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.SequenceInfo;
import tk.mybatis.mapper.common.Mapper;

public interface SequenceInfoMapper extends Mapper<SequenceInfo> {

    SequenceInfo getSequenceByName(String name);


}