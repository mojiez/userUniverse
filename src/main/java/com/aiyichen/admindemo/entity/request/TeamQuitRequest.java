package com.aiyichen.admindemo.entity.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class TeamQuitRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4043725273395691160L;
    /**
     * 要退出的队伍的id
     */
    private Long teamId;
}
