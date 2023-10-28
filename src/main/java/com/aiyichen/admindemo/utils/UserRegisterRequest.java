package com.aiyichen.admindemo.utils;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8114701589678835631L;
    // 实现序列化 String account,String password,String checkPassword
    private String account;
    private String password;
    private  String checkPassword;
}
