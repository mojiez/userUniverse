package com.aiyichen.admindemo.entity.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class TeamAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7964811841000382678L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码 为什么用户创建房间需要密码
     * 确实需要 看情况
     */
    private String password;
}
