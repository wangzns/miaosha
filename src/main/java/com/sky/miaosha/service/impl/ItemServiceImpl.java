package com.sky.miaosha.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.miaosha.dao.ItemMapper;
import com.sky.miaosha.dao.ItemStockMapper;
import com.sky.miaosha.dao.RocketmqTransactionLogMapper;
import com.sky.miaosha.dataobject.Item;
import com.sky.miaosha.dataobject.ItemStock;
import com.sky.miaosha.dataobject.RocketmqTransactionLog;
import com.sky.miaosha.exception.BusinessException;
import com.sky.miaosha.exception.enums.ExceptionEnum;
import com.sky.miaosha.exception.enums.StockLogStatusEnum;
import com.sky.miaosha.mq.MyProducer;
import com.sky.miaosha.service.ItemService;
import com.sky.miaosha.service.PromoService;
import com.sky.miaosha.service.model.ItemModel;
import com.sky.miaosha.service.model.PromoModel;
import com.sky.miaosha.utils.CommonUtil;
import com.sky.miaosha.utils.Convert;
import com.sky.miaosha.validator.ValidationResult;
import com.sky.miaosha.validator.ValidationTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidationTool validator;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemStockMapper itemStockMapper;

    @Autowired
    private PromoService promoService;

    @Autowired
    private MyProducer myProducer;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    /**
     * 创建商品
     */
    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) {

        //校验入参(插入数据库时要先检查参数是否合法)
        ValidationResult result = validator.validate(itemModel);
        if (result.hasErrors()) {
            throw new BusinessException(ExceptionEnum.PARAM_ERROR, result.getErrorDetail());
        }
        //ItemModel转换成Item
        Item item = Convert.convertItemFromItemModel(itemModel);

        //写入数据库
        itemMapper.insertSelective(item);

        //因为前端传过来的参数没有id这个参数，使用Item.getId()可以获取插入数据的id
        itemModel.setId(item.getId());

        //ItemModel转换成ItemStock
        ItemStock itemStock = Convert.convertItemStockFromItemModel(itemModel);
        itemStockMapper.insertSelective(itemStock);

        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }


    /**
     * 商品列表
     */
    @Override
    public List<ItemModel> listItem() {
        List<Item> itemList = itemMapper.listItem();
        List<ItemModel> itemModelList = itemList.stream().map(item -> {
            ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());
            //Item+ItemStock——>ItemModel
            ItemModel itemModel = Convert.convertModelFromItemAndItemStock(item, itemStock);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    /**
     * 根据id获取itemModel对象
     */
    @Override
    public ItemModel getItemById(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        if (item == null) {
            return null;
        }
        ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());

        //把Item和ItemStock转换成itemModel
        ItemModel itemModel = Convert.convertModelFromItemAndItemStock(item, itemStock);

        //获取商品活动信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if (promoModel != null && promoModel.getStatus().intValue() != 3) {
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    /**
     * 减库存
     */
    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        //返回影响函数 等于0表示减库存失败

        Long num = stringRedisTemplate.opsForValue().increment("promo_item_stock" + itemId, amount * -1);
        // num: 减少之后的库存
        if (num > 0) {
            // 异步减库存
            return true;
        } else if (num == 0) {
            // 商品已售罄
            stringRedisTemplate.opsForValue().set("promo_item_stock_invalid" + itemId, "true");
            return true;
        } else {
            // 库存不足，下单失败
            return false;
        }
    }

    @Override
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemMapper.increaseSales(itemId, amount);
    }

    @Override
    public ItemModel getItemByIdFromCache(Integer itemId) {
        String s = stringRedisTemplate.opsForValue().get("item_validate_" + itemId);
        ItemModel itemModel = JSON.parseObject(s, ItemModel.class);
        if (itemModel != null) {
            return itemModel;
        }
        ItemModel item = getItemById(itemId);
        if (item != null) {
            stringRedisTemplate.opsForValue().set("item_validate_" + itemId, JSON.toJSONString(item), 10, TimeUnit.MINUTES);
        }
        return item;
    }

    @Override
    public Boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        boolean sendResult = myProducer.asyncReduceStock(itemId, amount);
        return sendResult;

    }

    @Override
    public void increaseStock(Integer itemId, Integer amount) {
        stringRedisTemplate.opsForValue().increment("promo_item_stock" + itemId, amount);
    }

    @Override
    public String initStockStatus(Integer itemId, Integer amount) {
        RocketmqTransactionLog log = new RocketmqTransactionLog();
        String uuid = CommonUtil.randomUUID();
        log.setTransactionId(uuid);
        log.setAmount(amount);
        log.setItemId(itemId);
        log.setStatus(StockLogStatusEnum.INIT.getCode());
        rocketmqTransactionLogMapper.insertSelective(log);
        return uuid;
    }
}
