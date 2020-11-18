package com.sky.miaosha.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.miaosha.dao.UserInfoMapper;
import com.sky.miaosha.dao.UserPasswordMapper;
import com.sky.miaosha.dataobject.UserInfo;
import com.sky.miaosha.dataobject.UserPassword;

import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.service.UserService;
import com.sky.miaosha.service.model.UserModel;
import com.sky.miaosha.utils.Convert;
import com.sky.miaosha.validator.ValidationResult;
import com.sky.miaosha.validator.ValidationTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidationTool validator;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 根据id获取用户信息
     */
    @Override
    public UserModel getUser(Integer id) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
        if (userInfo == null) {
            return null;
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        return Convert.convertUserModelFromUserInfoAndUserPassword(userInfo, userPassword);
    }

    /**
     * 注册用户
     */
    @Override
    @Transactional
    public void register(UserModel userModel) {
        if (userModel == null) {
            throw new BusinessException(ExceptionEnum.PARAM_ERROR);
        }

        //校验
        ValidationResult result = validator.validate(userModel);
        if (result.hasErrors()) {
            throw new BusinessException(ExceptionEnum.PARAM_ERROR, result.getErrorDetail());
        }

        UserInfo userInfo = Convert.convertUserInfoFromUserModel(userModel);

        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("telphone", userInfo.getTelphone());
        List<UserInfo> thisTelphoneUser = userInfoMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(thisTelphoneUser)) {
            throw new BusinessException(ExceptionEnum.PARAM_ERROR, "手机号已注册");
        } else {
            userInfoMapper.insertSelective(userInfo);
        }
        userModel.setId(userInfo.getId());
        UserPassword userPassword = Convert.convertUserPasswordFromUserModel(userModel);
        userPasswordMapper.insertSelective(userPassword);

    }

    /**
     * 验证用户手机号和密码
     */
    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        //通过手机号获取用户信息
        UserInfo userInfo = userInfoMapper.selectByTelphone(telphone);
        if (userInfo == null) {
            throw new BusinessException(ExceptionEnum.USER_LOGIN_FAIL);
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        UserModel userModel = Convert.convertUserModelFromUserInfoAndUserPassword(userInfo, userPassword);

        //对比密码是否匹配
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BusinessException(ExceptionEnum.PASSWORD_NOT_MATCH);
        }
        return userModel;
    }

    @Override
    public UserModel getUserFromCache(Integer userId) {
        String s = stringRedisTemplate.opsForValue().get("user_validate_" + userId);
        UserModel userModel = JSON.parseObject(s, UserModel.class);
        if (userModel == null) {
            UserModel user = getUser(userId);
            stringRedisTemplate.opsForValue().set("user_validate_" + userId, JSON.toJSONString(user), 10, TimeUnit.MINUTES);
            return user;
        }
        return userModel;
    }


}
