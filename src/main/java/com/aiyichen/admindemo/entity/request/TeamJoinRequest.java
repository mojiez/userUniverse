package com.aiyichen.admindemo.entity.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class TeamJoinRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6898741202375594458L;
    /**
     * id
     */
    private Long id;
    /**
     * 密码
     */
    private String password;
}
