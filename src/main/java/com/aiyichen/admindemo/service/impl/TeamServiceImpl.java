package com.aiyichen.admindemo.service.impl;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.entity.UserTeam;
import com.aiyichen.admindemo.enums.TeamStatusEnum;
import com.aiyichen.admindemo.exception.BusinessException;
import com.aiyichen.admindemo.service.UserTeamService;
import com.aiyichen.admindemo.utils.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiyichen.admindemo.entity.Team;
import com.aiyichen.admindemo.service.TeamService;
import com.aiyichen.admindemo.mapper.TeamMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 *
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{
    @Resource
    private UserTeamService userTeamService;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addTeam(Team team, User loginUser) {
        // 1. 请求参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2. 是否登陆 未登陆不得创建
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户没有登陆 无权限");
        }
        // 一个变量被生命为final 则该变量的值不能被修改 一旦被赋值 它就成为常量 不能被重新赋值
        final Long id = loginUser.getId();
        // 3. 校验信息
        // a. 队伍人数 >=1 且 <=20
        Integer max_num = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (max_num <1 || max_num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合要求");
        }
        // b. 队伍标题 <= 20
        String name = team.getName();
        // is_blank用于判断字符串是否为空或者仅包含空格 制表符 换行符等空白字符
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名字不符合要求");
        }
        // c. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isBlank(description) || description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不符合要求");
        }
        // d. status 是否公开
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumbyValue(status);
        if (statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");
        }
        // e. 如果是加密状态 则一定要有密码 且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            // 状态是加密状态
            if (StringUtils.isBlank(password) || password.length()>32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
            }
        }
        // f. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            // 当前的之间在失效时间之后
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"保质期 < 当前时间");
        }
        // g. 校验用户最多创建5支队伍
        // TODO 有bug 如果一个用户在创建的时候猛点 可能同时创建100支队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",loginUser.getId());
        long count = this.count(queryWrapper);
        if (count >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }
        // 4. 插入信息到队伍表
        team.setId(null);
        team.setUserId(loginUser.getId());
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"插入队伍表失败");
        }
        // 5. 插入信息到用户关系表 4，5应该是一个事务 要做就一起做 这是维护关系表的特点
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean save1 = userTeamService.save(userTeam);
        if (!save1) throw new BusinessException(ErrorCode.PARAMS_ERROR,"插入用户队伍表失败");
        return teamId;
    }
}




