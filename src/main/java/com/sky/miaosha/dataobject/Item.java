package com.sky.miaosha.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 商品名
     */
    private String title;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品销量
     */
    private Integer sales;

    /**
     * 商品详情图地址
     */
    @Column(name = "img_url")
    private String imgUrl;

}