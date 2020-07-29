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
@Table(name = "sequence_info")
public class SequenceInfo {
    /**
     * 序列名
     */
    @Id
    private String name;

    /**
     * 当前值
     */
    @Column(name = "current_value")
    private Integer currentValue;

    /**
     * 步长
     */
    private Integer step;


}