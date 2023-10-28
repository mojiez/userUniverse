package com.aiyichen.admindemo.controller;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.service.UserService;
import com.aiyichen.admindemo.utils.R;
import com.aiyichen.admindemo.utils.UserLoginRequest;
import com.aiyichen.admindemo.utils.UserRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
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
}
