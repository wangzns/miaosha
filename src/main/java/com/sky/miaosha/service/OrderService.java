package com.sky.miaosha.service;

import com.sky.miaosha.service.model.OrderModel;

public interface OrderService {
    //通过前端url发送的秒杀活动id，在下单接口中校验对应id是否属于对应商品。且活动已开启
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount);

}
