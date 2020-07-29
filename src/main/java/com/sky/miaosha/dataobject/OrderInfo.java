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
@Table(name = "order_info")
public class OrderInfo {
    @Id
    private String id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 商品id
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 商品单价
     */
    @Column(name = "item_price")
    private Double itemPrice;

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 订单总价
     */
    @Column(name = "order_price")
    private Double orderPrice;

    @Column(name = "promo_id")
    private Integer promoId;


}