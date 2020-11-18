package com.sky.miaosha.service.impl;

import com.sky.miaosha.dao.PromoMapper;
import com.sky.miaosha.dataobject.Promo;
import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.service.ItemService;
import com.sky.miaosha.service.PromoService;
import com.sky.miaosha.service.model.ItemModel;
import com.sky.miaosha.service.model.PromoModel;
import com.sky.miaosha.utils.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoMapper promoMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private StringRedisTemplate redisTemplate;

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
        }
    }


}
