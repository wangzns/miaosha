package com.sky.miaosha.mq;

import com.alibaba.fastjson.JSON;
import com.sky.miaosha.dao.ItemMapper;
import com.sky.miaosha.dao.ItemStockMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author : wang zns
 * @date : 2020-11-05
 */
@Component
public class MyConsumer {

    @Value("${mq.nameserver.addr}")
    private String mqNameserverAddr;
    @Value("${mq.topicname}")
    private String topicName;

    private DefaultMQPushConsumer consumer;
    @Autowired
    private ItemStockMapper itemStockMapper;


    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(mqNameserverAddr);
        consumer.setVipChannelEnabled(false);
        // 订阅的topic
        consumer.subscribe(topicName,"*");
        consumer.setConsumerGroup("stock_consumer_group");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 取到消息之后
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                Map<Integer, Integer> map = JSON.parseObject(body, Map.class);
                Integer itemId = map.get("itemId");
                Integer amount = map.get("amount");
                if (itemId != null && amount != null) {
                    itemStockMapper.decreaseStock(itemId, amount);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();


    }


}
