package com.sky.miaosha.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.miaosha.service.CacheService;
import com.sky.miaosha.service.ItemService;
import com.sky.miaosha.service.model.ItemModel;
import com.sky.miaosha.utils.Convert;
import com.sky.miaosha.vo.ItemVO;
import com.sky.miaosha.vo.global.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", origins = {"*"})
public class ItemController  {

    @Autowired
    private ItemService itemService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CacheService cacheService;

    /**
     * 创建商品
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseVO createItem(@RequestParam(name = "title") String title,
                                 @RequestParam(name = "description") String description,
                                 @RequestParam(name = "price") BigDecimal price,
                                 @RequestParam(name = "stock") Integer stock,
                                 @RequestParam(name = "imgUrl") String imgUrl)  {

        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = Convert.convertItemVOFromItemModel(itemModelForReturn);
        return ResponseVO.success(itemVO);
    }


    /**
     * 商品列表页
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO listItem() {

        List<ItemModel> itemModelList = itemService.listItem();

        //使用stream API 将list内的itemModel转换成itemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = Convert.convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return ResponseVO.success(itemVOList);
    }

    /**
     * 获取商品详情页
     */
    @RequestMapping(value = "/getItem", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getItem(@RequestParam(name = "id") Integer id) {

        /*
        * 多级缓存的使用
        *
        * 1. 本地热点缓存 ?
        * 2. redis集中式缓存 ?
        *
        * 数据库.
        * */

        ItemModel itemModel = null;
        itemModel = (ItemModel)cacheService.getFromCommonCache("item_" + id);
        if (itemModel == null) {
            String value = stringRedisTemplate.opsForValue().get("item_" + id);
            if (StringUtils.isEmpty(value)) {
                itemModel = itemService.getItemById(id);
                stringRedisTemplate.opsForValue().set("item_" + id, JSON.toJSONString(itemModel),
                        30, TimeUnit.MINUTES);
            } else {
                itemModel = JSONObject.parseObject(value, ItemModel.class);
            }
            cacheService.setCommonCache("item_" + id, itemModel);
        }
        ItemVO itemVO = Convert.convertItemVOFromItemModel(itemModel);
        return ResponseVO.success(itemVO);
    }



}
