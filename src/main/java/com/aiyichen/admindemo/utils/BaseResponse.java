package com.aiyichen.admindemo.utils;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private int code;
    private T data;
    private String message;
    private String description;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    //1. 生成get set方法：@Data
    //2. 生成构造函数
}
