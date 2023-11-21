package com.aiyichen.admindemo.controller;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.exception.BusinessException;
import com.aiyichen.admindemo.service.UserService;
import com.aiyichen.admindemo.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;

import static com.aiyichen.admindemo.constant.UserConstant.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000/"})
//@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    // 如果说要使⽤json格式的参数的话，我们最好封装⼀个对象来记录所有的请求参数，然后我们在request包下新增⼀个对象叫UserRegisterRequest
    @PostMapping("/register")
    public R userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        // @Request标签是为了将前端传过来的json数据和后段封装的类做关联
        if (userRegisterRequest == null){
            return R.error();
        }
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long id = userService.userRegister(account,password,checkPassword);
        return R.ok().data("id",id);
    }

    @GetMapping("/userList")
    public R getUserList(HttpServletRequest request){
        boolean role = getRole(request);
        if ((!role)) return R.error();
        List<User> users = userService.list();
        return R.ok().data("users",users);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        // 参数user： 要修改的用户
        // 还要传是谁提交的请求

        // 验证参数是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 鉴权
        User loginUser = userService.getLoginUser(request);
        // 这里的loginUser肯定不为空 为空已经抛出了

        // 鉴权放到updateUser里面一起处理
        int success = userService.updateUser(user, loginUser);
        return ResultUtil.success(success);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam List<String> tagList){
        // 使用@RequestParam注解相当于告诉服务器这个参数要从http请求中的url获取
        // http://example.com/search?tagList=value1&tagList=value2
        // 使用了@RequestParam 能从上述参数中获取到 tagList = [values1,values2]

        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUsersByTagsMem(tagList);
        return ResultUtil.success(users);
    }
    // 实现登陆接口
    @PostMapping("/login")
    public R userDoLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return R.error();
        }
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();
        User user = userService.userDoLogin(account, password, request);
        return R.ok().data("safetyUser",user);
    }

    // 实现注销接口
    @PostMapping("/logout")
    public R userLogout(HttpServletRequest request){
        if(request == null) return R.error();
        int i = userService.userLogout(request);
        return R.ok();
    }
    // 实现模糊查询
    @GetMapping("/search/{useraccount}")
    public R userSearch(@PathVariable String useraccount,HttpServletRequest request){
        boolean role = getRole(request);
        if (!role){
            return R.error();
        }
        // 有权限 开始查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("user_account",useraccount);
        List<User> list = userService.list(wrapper);
        return R.ok().data("userlist",list);
    }
    // 实现分页查询 TODO

    // 查询单一用户
    @GetMapping("/current")
    public R currentUser(HttpServletRequest request){
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)object;
        if(user==null){
            return R.error();
        }
        //如果 用户不等于空 那么应该去数据库里查找最新的 因为session里面的可能不是最新的
        User user1 = userService.getById(user.getId());
        return R.ok().data("currentUser",user1);
    }
    // 实现删除
    @PostMapping("/deleteUser") //根据id删除
    public R deleteUser(@RequestBody long id,HttpServletRequest request){
        boolean role = getRole(request);
        if(!role){
            return R.error();
        }
        // 有权限 开始删除
        boolean b = userService.removeById(id);
        if (!b) return R.error().message("有权限但是删除失败");
        return R.ok();
    }
    private boolean getRole(HttpServletRequest request){
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null || user.getRole() != ADMIN_ROLE){
            return false;
        }
        return true;
    }
}
