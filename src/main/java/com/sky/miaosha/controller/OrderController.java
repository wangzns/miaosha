package com.sky.miaosha.controller;


import com.alibaba.fastjson.JSON;
import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.mq.MyProducer;
import com.sky.miaosha.service.ItemService;
import com.sky.miaosha.service.OrderService;
import com.sky.miaosha.service.model.UserModel;
import com.sky.miaosha.vo.global.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", origins = {"*"})
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MyProducer myProducer;
    @Autowired
    private ItemService itemService;

    //封装下单请求
    @RequestMapping(value = "/createorder", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO createOrder(@RequestParam(name = "itemId") Integer itemId,
                                  @RequestParam(name = "amount") Integer amount,
                                  @RequestParam(name = "promoId", required = false) Integer promoId) throws BusinessException {

        //判断用户是否登录
//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token = httpServletRequest.getHeader("token");
        log.info("创建订单，token={}", token);
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(ExceptionEnum.USER_NOT_LOGIN, "用户还没登录，无法下单");
        }
        String str = redisTemplate.opsForValue().get(token);
        if (StringUtils.isEmpty(str)) {
            throw new BusinessException(ExceptionEnum.USER_NOT_LOGIN, "用户还没登录，无法下单");
        }
        Boolean aBoolean = redisTemplate.hasKey("promo_item_stock_invalid" + itemId);
        if (aBoolean) {
            // 已售罄
            throw new BusinessException(ExceptionEnum.ITEM_HAS_SEAL_OUT);
        }
        UserModel userModel = JSON.parseObject(str, UserModel.class);
        String transactionId = itemService.initStockStatus(itemId, amount);
        //获取登录的用户信息
//        orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        Boolean success = myProducer.transactionAsyncRedusceStock(userModel.getId(), itemId, promoId, amount, transactionId);
        if (success) {
            return ResponseVO.success();
        } else {
            return ResponseVO.fail("下单失败");
        }
    }
}
