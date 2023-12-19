package com.aiyichen.admindemo.controller;

import com.aiyichen.admindemo.entity.Team;
import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.entity.dto.TeamQuery;
import com.aiyichen.admindemo.entity.request.TeamAddRequest;
import com.aiyichen.admindemo.exception.BusinessException;
import com.aiyichen.admindemo.service.TeamService;
import com.aiyichen.admindemo.service.UserService;
import com.aiyichen.admindemo.utils.BaseResponse;
import com.aiyichen.admindemo.utils.ErrorCode;
import com.aiyichen.admindemo.utils.ResultUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
// requestmapping设置父路径
@RequestMapping("/team")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000/"},allowCredentials = "true")
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    // 增删改查

    /**
     * 增
     * @param team
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean save = teamService.save(team);
        if (!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"插入队伍失败");
        }
        // 成功返回插入队伍的id  这里有一个回传的技术 数据库把新生成的is又赋值给team的id属性
        return ResultUtil.success(team.getId());
    }

    /**
     * 删
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = teamService.removeById(id);
        if (!b){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"删除队伍失败");
        }
        return ResultUtil.success(true);
    }

    /**
     * 改
     * @param team
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean b = teamService.updateById(team);
        if (!b){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新队伍失败");
        }
        return ResultUtil.success(true);
    }
    @GetMapping("/list")
    public BaseResponse<List<Team>> getTeamList(TeamQuery teamQuery){
        // 根据条件来查询list
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(tea)
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);

        List<Team> list = teamService.list(queryWrapper);
        if (list == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"获取team list失败");
        }
        return ResultUtil.success(list);
    }
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamPage(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = teamService.page(new Page<>(1, 3), queryWrapper);
        if (page == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"获取team page失败");
        }
        return ResultUtil.success(page);
    }
    @PostMapping("/addteam")
    // @Requestbody Post请求中使用，接收前端传给后端的json字符串（将前端传递的json字符串转化为后端的对象）
    // 为什么需要request这个参数？ 需要获取当前用户是谁 因为一个用户只能创建5个队伍
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtil.success(teamId);
    }
}
