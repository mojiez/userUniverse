package com.aiyichen.admindemo.utils;

import lombok.Data;

// 自定义错误码
public enum ErrorCode {
    SUCCESS(0,"ok","success"),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求参数为空",""),
    NOT_LOGIN(40100,"未登陆",""),
    NO_AUTH(40101,"无权限","");

    private final int code;
    private final String message;
    private final String description;
    ErrorCode(int code,String message,String description){
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
    // 创建get方法，enum不能使用@Data注解
}
