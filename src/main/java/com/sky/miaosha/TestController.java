package com.sky.miaosha;

import com.sky.miaosha.dao.UserInfoMapper;
import com.sky.miaosha.dataobject.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 * jdk: 1.8
 */
@Controller
public class TestController {

    @Autowired
    private UserInfoMapper userInfoMapper;


    @RequestMapping("/user")
    @ResponseBody
    public String user() {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(1);
        if (userInfo == null) {
            return "用户不存在";
        }
        return userInfo.getName();
    }



}
