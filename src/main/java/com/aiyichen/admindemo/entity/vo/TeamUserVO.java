package com.aiyichen.admindemo.entity.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

// 查询队伍的返回结果的封装
@Data
public class TeamUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8355255464740867533L;
    /**
     * 队伍id
     */
    private Long id;

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
     * 用户id (创建这个队伍的用户id) TODO 为什么已经有UserVO了 还要userId
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVO createUser;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

    /**
     * 是否已加入队伍 TODO 这到底是干什么的
     */
    private boolean hasJoin = false;
}
