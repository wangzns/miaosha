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
@Table(name = "item_stock")
public class ItemStock {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 库存
     */
    private Integer stock;

    @Column(name = "item_id")
    private Integer itemId;


}