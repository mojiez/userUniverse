package com.aiyichen.admindemo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态（禁言之类的 0表示正常）
     */
    private Integer userState;

    /**
     * 电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Byte isDeleted;

    /**
     * 用户角色 0 普通用户 1 管理员 2VIP
     */
    private Integer role;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}