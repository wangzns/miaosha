package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.UserPassword;
import tk.mybatis.mapper.common.Mapper;

public interface UserPasswordMapper extends Mapper<UserPassword> {

    UserPassword selectByUserId(Integer userId);


}