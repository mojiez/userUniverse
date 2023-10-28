package com.aiyichen.admindemo.utils;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4149458608947935874L;
    // 生成序列码
//    String account, String password
    private String account;
    private String password;
}
