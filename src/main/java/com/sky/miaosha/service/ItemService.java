package com.sky.miaosha.service;


import com.sky.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel);

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount);

    //商品销量增加
    void increaseSales(Integer itemId, Integer amount);

    /**
     * 缓存中查询商品活动模型
     * @param itemId
     * @return
     */
    ItemModel getItemByIdFromCache(Integer itemId);

    /**
     * 异步更新库存
     * @param itemId
     * @param amount
     */
    Boolean asyncDecreaseStock(Integer itemId, Integer amount);

    void increaseStock(Integer itemId, Integer amount);



}
