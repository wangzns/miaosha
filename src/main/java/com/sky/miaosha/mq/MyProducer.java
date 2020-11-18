package com.sky.miaosha.mq;

import com.alibaba.fastjson.JSON;
import com.sky.miaosha.service.OrderService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : wang zns
 * @date : 2020-11-05
 */
@Component
public class MyProducer {

    /**
     * 默认型消息生产者
     */
    private DefaultMQProducer producer;
    /**
     * 事务型消息生产者
     */
    private TransactionMQProducer transactionMQProducer;
    @Autowired
    private OrderService orderService;

    @Value("${mq.nameserver.addr}")
    private String mqNameserverAddr;
    @Value("${mq.topicname}")
    private String topicName;


    /**
     * producer初始化
     * @throws MQClientException
     */
    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer("produce_group");
        producer.setNamesrvAddr(mqNameserverAddr);
        producer.setSendMsgTimeout(10000);
        producer.setVipChannelEnabled(false);
        producer.start();

        transactionMQProducer = new TransactionMQProducer("transaction_produce_group");
        transactionMQProducer.setNamesrvAddr(mqNameserverAddr);
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                // 执行本地事务. 该事务的执行结果将决定半消息(prepare)是否能被消费
                Map<String, Object> args= (Map) o;
                Integer userId = (Integer)args.get("userId");
                Integer itemId = (Integer)args.get("itemId");
                Integer promoId = (Integer)args.get("promoId");
                Integer amount = (Integer)args.get("amount");
                try {
                    orderService.createOrder(userId, itemId, promoId, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                // commit本地事务之后， 才允许被消费
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                return null;
            }
        });
        transactionMQProducer.start();;

    }

    /**
     * 事务性消息的发送
     * @param itemId
     * @param amount
     * @return
     */
    public boolean transactionAsyncRedusceStock(Integer userId, Integer itemId, Integer promoId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        Map<String ,Object> argsMap = new HashMap<>();
        argsMap.put("itemId", itemId);
        argsMap.put("userId", userId);
        argsMap.put("promoId", promoId);
        argsMap.put("amount", amount);
        Message message = new Message(topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        try {
            TransactionSendResult transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
            if (transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.COMMIT_MESSAGE)) {
                return true;
            }  else {
                return false;
            }
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean asyncReduceStock(Integer itemId, Integer amount) {

        try {
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("itemId", itemId);
            bodyMap.put("amount", amount);
            Message message = new Message(topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
            producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



}
