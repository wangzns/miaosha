package com.sky.miaosha.service;

import com.sky.miaosha.service.model.PromoModel;

public interface PromoService {

    //根据itemId获取即将进行或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);

    /**
     * 发布活动（活动上架）
     * @param promoId
     */
    void publishPromo(Integer promoId);

    /**
     * 生成秒杀令牌
     * @param promoId
     * @param itemId
     * @param userId
     * @return
     */
    String generatePromoToken(Integer promoId, Integer itemId, Integer userId);



}
