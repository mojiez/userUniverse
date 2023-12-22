package com.aiyichen.admindemo.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

// dto包就是 业务封装类
@Data
public class TeamQuery {
    // 为什么根据条件查询list要弄一个业务封装类呢
    // 1. 实体类的有一些参数用不到 如果自动生成接口文档 会增加理解成本

    // 为什么需要包装类？
    // 1.有一些字段需要隐藏 不能返回给前段
    // 2.某些字段某些方法是不关心的 不需要返回


    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;



    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 搜索关键词 （同时对name和description 进行搜索）
     */
    private String searchText;
}
