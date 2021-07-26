package com.lzp.entity;

import lombok.Data;

@Data
public class User {

    /*账户编号*/
    private int userid;

    /*账户名称*/
    private String username;

    /*账户密码*/
    private String password;

    /*账户余额*/
    private int account;
}
