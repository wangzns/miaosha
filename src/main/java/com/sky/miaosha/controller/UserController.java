package com.sky.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.sky.miaosha.vo.UserVO;
import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.service.UserService;
import com.sky.miaosha.service.model.UserModel;
import com.sky.miaosha.utils.Convert;
import com.sky.miaosha.vo.global.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", origins = {"*"})
@Slf4j
public class UserController  {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /*根据id获取用户信息*/
    @RequestMapping(value = "/get", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO getUser(@RequestParam(value = "id") Integer id)  {

        UserModel userModel = userService.getUser(id);
        if (userModel == null) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
        }
        //转换为给前端用的模型（隐藏了密码等）
        UserVO userVO = Convert.convertUserVOFromUserModel(userModel);
        return ResponseVO.success(userVO);
    }

    /*获取otp验证码*/
    @RequestMapping(value = "/getotp", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO getOtp(@RequestParam(value = "telphone") String telphone) {
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        httpServletRequest.getSession().setAttribute(telphone, otpCode);
        log.info("otpCode={} , telphone={}" ,otpCode,telphone);
        return ResponseVO.success();
    }

    /*用户注册*/
    @RequestMapping(value = "/register", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和验证码
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(ExceptionEnum.PARAM_ERROR, "短信验证码错误！");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);
        return ResponseVO.success();
    }

    /*用户登录*/
    @RequestMapping(value = "/login", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)
                || StringUtils.isEmpty(password)) {
            throw new BusinessException(ExceptionEnum.PARAM_ERROR);
        }
        //校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));
        //把用户信息放入session
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        return ResponseVO.success();

    }

    //对密码进行加密
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }


}
