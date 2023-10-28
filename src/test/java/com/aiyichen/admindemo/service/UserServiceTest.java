package com.aiyichen.admindemo.service;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;
    @Test
    public void addUserTest(){
        User user = new User();
        user.setUserName("zhangsan");
        user.setUserAccount("12345");
        user.setUserPassword("0");


        user.setEmail("1@qq.com");
        user.setGender((byte) 0);
        user.setPhone("123");
        user.setAvatarUrl("");


        boolean save = userService.save(user);
        Assertions.assertEquals(true,save);
        System.out.println(user.getId());
    }
    @Test
    public void selectUserisDeleted(){
//        userService.userDoLogin("wyc250",)
        QueryWrapper<User>wrapper = new QueryWrapper<>();
        wrapper.eq("user_account","wyc250");
        User user = userMapper.selectOne(wrapper);
        if(user == null){
            System.out.println("没有搜到");
        }else {
            System.out.println(user);
        }
    }
}