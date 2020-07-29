package com.sky.miaosha.utils;

import com.sky.miaosha.vo.ItemVO;
import com.sky.miaosha.vo.UserVO;
import com.sky.miaosha.dataobject.*;
import com.sky.miaosha.service.model.ItemModel;
import com.sky.miaosha.service.model.OrderModel;
import com.sky.miaosha.service.model.PromoModel;
import com.sky.miaosha.service.model.UserModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

public class Convert {

    //ItemModel——>ItemStock
    public static ItemStock convertItemStockFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStock itemStock = new ItemStock();
        itemStock.setItemId(itemModel.getId());
        itemStock.setStock(itemModel.getStock());
        return itemStock;
    }

    //ItemModel——>Item
    public static Item convertItemFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        Item item = new Item();
        BeanUtils.copyProperties(itemModel, item);
        //ItemModel中price是BigDecimal类型的，需要手动转换成double类型（不同类型无法复制）
        item.setPrice(itemModel.getPrice().doubleValue());
        return item;
    }

    //Item+ItemStock——>ItemModel
    public static ItemModel convertModelFromItemAndItemStock(Item item, ItemStock itemStock) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(item, itemModel);
        itemModel.setPrice(new BigDecimal(item.getPrice()));
        itemModel.setStock(itemStock.getStock());
        return itemModel;
    }



    //UserModel———>UserInfo
    public static UserInfo convertUserInfoFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userModel, userInfo);
        return userInfo;
    }

    //UserModel————>UserPassword
    public static UserPassword convertUserPasswordFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPassword userPassword = new UserPassword();
        userPassword.setEncrptPassword(userModel.getEncrptPassword());
        userPassword.setUserId(userModel.getId());
        return userPassword;
    }

    //UserInfo+UserPassword————>UserModel(因为密码放在另一张表里，UserModel加入了另一张表的数据)
    public static UserModel convertUserModelFromUserInfoAndUserPassword(UserInfo userInfo, UserPassword userPassword) {
        if (userInfo == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo, userModel);
        if (userPassword != null) {
            userModel.setEncrptPassword(userPassword.getEncrptPassword());
        }
        return userModel;
    }


    //Promo————>PromoModel
    public static PromoModel convertPromoModelFromPromo(Promo promo) {
        if (promo == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promo, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promo.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promo.getStartDate()));
        promoModel.setEndDate(new DateTime(promo.getEndDate()));
        return promoModel;
    }

    //OrderModel——>OrderInfo
    public static OrderInfo convertOrderInfoFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderModel, orderInfo);
        orderInfo.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderInfo.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderInfo;
    }

    //把UserModel转换成UserVO
    public static UserVO convertUserVOFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

    //ItemModel——>ItemVO
    public static ItemVO convertItemVOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if (itemModel.getPromoModel() != null) {
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
