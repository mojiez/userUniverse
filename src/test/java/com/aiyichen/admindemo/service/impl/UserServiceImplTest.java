package com.aiyichen.admindemo.service.impl;

import com.aiyichen.admindemo.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    // 注册逻辑测试成功 证明了插入成功后实例里面本来没有的属性值会自动填充
    @Test
    void userRegister() {
        String userAccount = "zkj";
        String userPassword = "12345678";
        String checkPassword = "12345678";
//        long id = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,id);
//
//        userAccount = "zkj  ";
//        id = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,id);

        userAccount = "wyc250";
        long id = userService.userRegister(userAccount,userPassword,checkPassword);
        System.out.println(id);
        Assertions.assertTrue(id>0);

    }

    @Test
    void searchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java","python","guonan");
        List<User> users = userService.searchUsersByTags(tagNameList);
        for(User user:users){
            System.out.println(user.getUserName());
        }
    }

    @Test
    void searchUsersByTagsMem() {
        List<String> tagNameList = Arrays.asList("java","python","guonan");
        List<User> users = userService.searchUsersByTagsMem(tagNameList);
        for (User user:users){
            System.out.println(user.getUserName());
        }
    }
}