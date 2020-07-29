package com.sky.miaosha.controller;


import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.service.OrderService;
import com.sky.miaosha.service.model.UserModel;
import com.sky.miaosha.vo.global.ResponseVO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", origins = {"*"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //封装下单请求
    @RequestMapping(value = "/createorder", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO createOrder(@RequestParam(name = "itemId") Integer itemId,
                                  @RequestParam(name = "amount") Integer amount,
                                  @RequestParam(name = "promoId", required = false) Integer promoId) throws BusinessException {

        //判断用户是否登录
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin.booleanValue()) {
            throw new BusinessException(ExceptionEnum.USER_NOT_LOGIN, "用户还没登录，无法下单");
        }
        //获取登录的用户信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        return ResponseVO.success();
    }
}
