package com.aiyichen.admindemo.utils;

public class ResultUtil {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }
    public static BaseResponse error(ErrorCode errorCode,String message,String descrption){
        return new BaseResponse(errorCode.getCode(),null,message,descrption);
    }
    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse(code,null,message,description);
    }
}
