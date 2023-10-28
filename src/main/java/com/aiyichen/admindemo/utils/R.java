package com.aiyichen.admindemo.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

// 定义统一返回结果
@Data
public class R {
    private Boolean success;
    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<String,Object>();

    // 私有化构造函数
    private R(){}

    // 成功静态方法
    public static R ok(){
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }
    // 失败静态方法
    // 使用静态方法构造实例
    public static R error(){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    // 完成链式设置
    // 不用静态方法，因为静态方法是类维护的 当使用了R.ok()以后 返回的其实是一个实例
    // 这时候想调用方法设置data的话就不能用静态方法了
    // 实例也可以调用静态方法 但是静态方法不能访问这个实例的成员变量
    public  R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    //  设置data 链式编程
    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public R data(Map<String,Object> map){
        this.setData(map);
        return this;
    }
}
