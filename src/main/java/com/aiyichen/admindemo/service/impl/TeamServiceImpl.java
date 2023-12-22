package com.aiyichen.admindemo.service.impl;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.entity.UserTeam;
import com.aiyichen.admindemo.entity.dto.TeamQuery;
import com.aiyichen.admindemo.entity.request.TeamJoinRequest;
import com.aiyichen.admindemo.entity.request.TeamUpdateRequest;
import com.aiyichen.admindemo.entity.vo.TeamUserVO;
import com.aiyichen.admindemo.entity.vo.UserVO;
import com.aiyichen.admindemo.enums.TeamStatusEnum;
import com.aiyichen.admindemo.exception.BusinessException;
import com.aiyichen.admindemo.service.UserService;
import com.aiyichen.admindemo.service.UserTeamService;
import com.aiyichen.admindemo.utils.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiyichen.admindemo.entity.Team;
import com.aiyichen.admindemo.service.TeamService;
import com.aiyichen.admindemo.mapper.TeamMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
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

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 1. 从请求的参数中取出队伍名称等查询条件，如果存在则作为查询条件
        if (teamQuery != null){
            Long id = teamQuery.getId();
            if (id != null && id>0){
                queryWrapper.eq("id",id);
            }
            // 搜索关键词(同时对队伍名称和描述搜索)
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)){
                // and 方法表示进行and连接 后面可以连多个条件
                // qw-> 是一个lambda表达式 表示一个新的querywrapper对象
                // like 模糊查询
                // .or 表示在当前条件下进行or连接
                // 这里的and方法表示的事 当前条件和后面的lambda表达式是一个 and 的关系
                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的队伍
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num",maxNum);
            }

            // 根据创建人来查询
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("user_id",userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumbyValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            // 如果 当前用户不是管理员 但是 我要查的是非公开的房间的话 就抛异常
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH,"没有权限查非公开的房间");
            }
            queryWrapper.eq("status",statusEnum.getValue());
        }
        // 2， 不展示已过期的队伍（根据过期时间筛选）
        // 和之前的条件and 过期时间要比当前的时间长
        // 如果写在括号外面 就是左右两边或
        // 如果写在括号里面 括号里面的东西和前面的条件组成 ||
        queryWrapper.and(qw -> qw.gt("expire_time",new Date()).or().isNull("expire_time"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                // userVO里面保存就是创建的用户的信息
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
        // 3. 可以通过某个关键词同时对name 和 description查询
        // 4. 只有管理员才能查看加密还有非公开的房间（只有管理员才能搜索加密或者非公开的房间）
        // 5. **关联查询已经加入队伍的用户信息**

    }

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 1. 判断请求参数是否为空
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        // 2. 查询队伍是否存在
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        // 3. 只有管理员或者队伍的创建着可以修改
        if (oldTeam.getUserId() != loginUser.getId() || !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // TODO 如果用户传入的新值和老值 就不用update了 降低数据库使用次数
        // 5. 如果队伍状态改为加密 必须要有密码
        TeamStatusEnum newTeamStatus = TeamStatusEnum.getEnumbyValue(teamUpdateRequest.getStatus());
        if (newTeamStatus.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        // 6. 更新成功
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamJoinRequest.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查队伍
        Team teamToJoin = this.getById(teamId);
        if (teamToJoin == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"要加入的队伍不存在");
        }
        Date expireTime = teamToJoin.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"要加入的队伍已过期");
        }
        Integer status = teamToJoin.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumbyValue(status);
        if (statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有的队伍");
        }
        String myPassword = teamJoinRequest.getPassword();
        String password = teamToJoin.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(myPassword) || !myPassword.equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }
        // 一个用户只能加入五只队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        long hasJoinNum = userTeamService.count(queryWrapper);
        if (hasJoinNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多加入五只队伍");
        }
        // 不能重复加入已经加入的队伍
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id",userId).eq("team_id",teamId);
        long hasUserJoinTeam = userTeamService.count(queryWrapper1);
        if (hasUserJoinTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复加入已加入的队伍");
        }
        // 加入队伍的人数不能超过最大的人数
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id",teamId);
        long teamHasJoinNum = userTeamService.count(queryWrapper);
        if (teamHasJoinNum >= teamToJoin.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        //加入 修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
        // TODO 重复加入队伍的问题 （加锁 分布式锁）


    }
}




