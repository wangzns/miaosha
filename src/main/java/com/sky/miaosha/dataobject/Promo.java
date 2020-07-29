package com.sky.miaosha.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "promo")
public class Promo {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 秒杀活动名称
     */
    @Column(name = "promo_name")
    private String promoName;

    /**
     * 秒杀活动开始时间
     */
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 秒杀活动结束时间
     */
    @Column(name = "end_date")
    private Date endDate;

    /**
     * 秒杀商品id
     */
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 秒杀商品价格
     */
    @Column(name = "promo_item_price")
    private Double promoItemPrice;


}