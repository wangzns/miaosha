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
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String name;

    /**
     * 性别 1.男 2.女
     */
    private Byte gender;

    /**
     * 年龄
     */
    private Integer age;

    private String telphone;

    @Column(name = "register_mode")
    private String registerMode;

    @Column(name = "third_party_id")
    private String thirdPartyId;


}