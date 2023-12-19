package com.aiyichen.admindemo.service;

import com.aiyichen.admindemo.entity.Team;
import com.aiyichen.admindemo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);
}
