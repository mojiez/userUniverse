package com.aiyichen.admindemo.constant;

public interface UserConstant {
    //默认是public static final的

    // 登陆状态关键字 保存在session中
    String USER_LOGIN_STATE = "userLoginState";

    // 用户默认权限
    int DEFAULT_ROLE = 0;

    // 管理员权限
    int ADMIN_ROLE = 1;
}
