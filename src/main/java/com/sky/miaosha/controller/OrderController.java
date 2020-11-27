package com.sky.miaosha.controller;


import com.alibaba.fastjson.JSON;
import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.mq.MyProducer;
import com.sky.miaosha.service.ItemService;
import com.sky.miaosha.service.OrderService;
import com.sky.miaosha.service.PromoService;
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

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    @Autowired
    private PromoService promoService;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        // 最大容量为20个线程的线程池. 拥塞窗口=20
        executorService = Executors.newFixedThreadPool(20);
    }


    // 生成秒杀令牌
    @RequestMapping(value = "/generatetoken", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO generatetoken(@RequestParam(name = "itemId") Integer itemId,
                                  @RequestParam(name = "promoId", required = false) Integer promoId) throws BusinessException {
        String token = httpServletRequest.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(ExceptionEnum.USER_NOT_LOGIN, "用户还没登录");
        }
        String str = redisTemplate.opsForValue().get(token);
        if (StringUtils.isEmpty(str)) {
            throw new BusinessException(ExceptionEnum.USER_NOT_LOGIN, "用户还没登录");
        }
        Long leftTimes = redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
        if (leftTimes < 0) {
            throw new BusinessException(ExceptionEnum.PROMO_TOKEN_NOT_ENOUGH);
        }
        UserModel userModel = JSON.parseObject(str, UserModel.class);
        String promoToken = promoService.generatePromoToken(promoId, itemId, userModel.getId());
        if (StringUtils.isEmpty(promoToken)) {
            // 为空则表示不满足生成令牌的必要条件， 可能是用户风控，库存，伪造请求等多种原因造成。不允许进行秒杀
            throw new BusinessException(ExceptionEnum.PROMO_TOKEN_GENERATE_FAIL);
        }
        return ResponseVO.success(promoToken);

    }

    //封装下单请求
    @RequestMapping(value = "/createorder", method = RequestMethod.POST,  consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO createOrder(@RequestParam(name = "itemId") Integer itemId,
                                  @RequestParam(name = "amount") Integer amount,
                                  @RequestParam(name = "promoId", required = false) Integer promoId,
                                  @RequestParam(name = "promoToken", required = false) String promoToken) throws BusinessException {

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
        UserModel userModel = JSON.parseObject(str, UserModel.class);

        if (promoId != null) {
            // 有秒杀活动， 需要鉴定promoToken
            if (StringUtils.isEmpty(promoToken)) {
                throw new BusinessException(ExceptionEnum.PROMO_TOKEN_NOT_EXIST);
            }
            String key = "promo_item_token" + "_promoid_" + promoId + "_itemid_" + itemId + "_userid_" + userModel.getId();
            String inRedisPromoToken = redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(inRedisPromoToken)) {
                throw new BusinessException(ExceptionEnum.PROMO_TOKEN_NOT_EXIST);
            }
            if (!promoToken.equals(inRedisPromoToken)) {
                throw new BusinessException(ExceptionEnum.PROMO_TOKEN_NOT_EXIST);
            }
        }
        Future<Boolean> future = executorService.submit(() -> {
            String transactionId = itemService.initStockStatus(itemId, amount);
            //获取登录的用户信息
//        orderService.createOrder(userModel.getId(), itemId, promoId, amount);
            Boolean result = myProducer.transactionAsyncRedusceStock(userModel.getId(), itemId, promoId, amount, transactionId);
            return result;
        });
        try {
            Boolean orderResult = future.get();
            if (orderResult) {
                return ResponseVO.success();
            } else {
                return ResponseVO.fail("下单失败");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new BusinessException(ExceptionEnum.UNKNOW_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ResponseVO.fail("下单失败");
    }




}
