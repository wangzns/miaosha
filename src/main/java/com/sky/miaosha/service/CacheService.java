package com.sky.miaosha.service;

/**
 * @author : wang zns
 * @date : 2020-08-05
 */
public interface CacheService {

    /**
     * 设置本地缓存
     * @param key
     * @param value
     */
    void setCommonCache(String key, Object value);

    /**
     * 从本地缓存中取值
     * @param key
     * @return
     */
    Object getFromCommonCache(String key);
}
