package com.sky.miaosha.service;

import com.sky.miaosha.service.model.UserModel;

public interface UserService {

    UserModel getUser(Integer id);

    void register(UserModel userModel);

    UserModel validateLogin(String telphone, String encrptPassword);

    UserModel getUserFromCache(Integer userId);
}
