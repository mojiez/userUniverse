package com.aiyichen.admindemo.service;

import com.aiyichen.admindemo.entity.Team;
import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.entity.dto.TeamQuery;
import com.aiyichen.admindemo.entity.request.TeamJoinRequest;
import com.aiyichen.admindemo.entity.request.TeamUpdateRequest;
import com.aiyichen.admindemo.entity.vo.TeamUserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
