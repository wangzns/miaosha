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
@Table(name = "user_password")
public class UserPassword {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 加密后的密码
     */
    @Column(name = "encrpt_password")
    private String encrptPassword;

    @Column(name = "user_id")
    private Integer userId;

}