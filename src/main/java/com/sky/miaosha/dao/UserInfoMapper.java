package com.sky.miaosha.dao;

import com.sky.miaosha.dataobject.UserInfo;
import tk.mybatis.mapper.common.Mapper;

public interface UserInfoMapper extends Mapper<UserInfo> {

    UserInfo selectByTelphone(String telphone);



}