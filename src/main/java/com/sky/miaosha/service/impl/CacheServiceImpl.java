package com.sky.miaosha.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sky.miaosha.service.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 本地热点缓存操作类
 * 该缓存的必须要有的特点： 1. 热点数据 2. 脏读的容忍性极高 3. 有效时间不能很长
 * @author : wang zns
 * @date : 2020-08-05
 */
@Service
public class CacheServiceImpl implements CacheService {

    /*
    *
    * 使用guava工具类做本地热点缓存.
    * guava cache的优点如下：
    *
    * 1. 可控制的大小和超时时间
    * 2. 可配置的 lru策略
    * 3. 线程安全
    *
    * */


    private Cache<String, Object> cache = null;

    @PostConstruct
    public void init() {
        cache  = CacheBuilder.newBuilder()
                // 初始化容量
                .initialCapacity(10)
                // 最大容纳的缓存节点数，超过该值后按照lru(最近最少使用)策略进行淘汰
                .maximumSize(100)
                // 缓存有效时间
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();

    }

    @Override
    public void setCommonCache(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return cache.getIfPresent(key);
    }
}
