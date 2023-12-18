package com.aiyichen.admindemo;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {
    // 测试配置好的redission 需要引入redisson
    @Resource
    private RedissonClient redissonClient;
    @Test
    void test(){
        // 正常的list操作
        List<String> list = new ArrayList<>();
        list.add("caonima");
        list.add("diuleiloumou");

        // 将list数据存到redis中 通过redisson
        // 获取一个list
        RList<String> testList = redissonClient.getList("test_list1");
        testList.add("caonima");
        testList.add("diuleiloumou");

    }
}
