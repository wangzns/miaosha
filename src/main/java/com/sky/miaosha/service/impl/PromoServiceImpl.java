package com.sky.miaosha.service.impl;

import com.sky.miaosha.dao.PromoMapper;
import com.sky.miaosha.dataobject.Promo;
import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.service.ItemService;
import com.sky.miaosha.service.PromoService;
import com.sky.miaosha.service.UserService;
import com.sky.miaosha.service.model.ItemModel;
import com.sky.miaosha.service.model.PromoModel;
import com.sky.miaosha.service.model.UserModel;
import com.sky.miaosha.utils.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoMapper promoMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {

        //获取对应商品的秒杀活动信息
        Promo promo = promoMapper.selectByItemId(itemId);
        if (promo == null) {
            return null;
        }
        //Promo————>PromoModel
        PromoModel promoModel = Convert.convertPromoModelFromPromo(promo);

        //判断当前时间是否秒杀活动即将开始或正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    @Override
    public void publishPromo(Integer promoId) {
        Promo promo = promoMapper.selectByPrimaryKey(promoId);
        if (promo == null) {
            throw new BusinessException(ExceptionEnum.PROMO_NOT_EXIST);
        }
        Integer itemId = promo.getItemId();
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel != null) {
            redisTemplate.opsForValue().set("promo_item_stock"+itemId, itemModel.getStock().toString());
            // 控制秒杀大闸的数量为商品库存的2倍
            int i = itemModel.getStock().intValue();
            int count = i * 2;
            redisTemplate.opsForValue().set("promo_door_count_"+ promoId, String.valueOf(count));

        }
    }

    @Override
    public String generatePromoToken(Integer promoId, Integer itemId, Integer userId) {
        Boolean aBoolean = redisTemplate.hasKey("promo_item_stock_invalid" + itemId);
        if (aBoolean) {
            // 已售罄
            return null;
        }
        ItemModel itemModel = itemService.getItemByIdFromCache(itemId);
        if (itemModel == null) {
            // 商品信息不存在
            return null;
        }

        Promo promo = promoMapper.selectByPrimaryKey(promoId);
        if (promo == null) {
            // 活动信息不存在
            return null;
        }

        //Promo————>PromoModel
        PromoModel promoModel = Convert.convertPromoModelFromPromo(promo);

        // 判断当前时间是否秒杀活动即将开始或正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        if (promoModel.getStatus() != 2) {
            // 活动未开始
            return null;
        }
        UserModel userModel = userService.getUserFromCache(userId);
        if (userModel == null) {
            //  用户为空
            return null;
        }
        String promoToken = UUID.randomUUID().toString().replace("-","");
        String key = "promo_item_token" + "_promoid_" + promoId + "_itemid_" + itemId + "_userid_" + userId;
        redisTemplate.opsForValue().set(key, promoToken, 5, TimeUnit.MINUTES);
        return promoToken;
    }


}
