package com.aiyichen.admindemo;

import com.aiyichen.admindemo.entity.User;

import com.aiyichen.admindemo.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
public class mpCRUDTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void mpSelectTest(){
        List<User> users = userMapper.selectList(null);
        Assertions.assertEquals(5,users.size());
        users.forEach(System.out::println);
    }
}
