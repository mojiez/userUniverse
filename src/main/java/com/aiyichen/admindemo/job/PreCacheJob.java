package com.aiyichen.admindemo.job;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jodd.util.CsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {
    // 定时任务 在0点的时候 为每个重点用户都查一边 然后将结果放入redis中
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    // 每天执行
    // 预热推荐用户

    /**
     * 现在引入了定时任务 设想一下 如果最终这个项目要部署在多态服务器上的话 每个服务器都会执行这样的定时任务
     * 这显然是我不需要的
     * 由此引入分布式锁（只有抢到锁的服务器才能执行业务逻辑）
     *
     * 使用redission实现分布式锁！！！！！
     */
    @Scheduled(cron = "0 12 1 * * *")
    public void doCacheRecommendUser(){
        QueryWrapper<User>queryWrapper = new QueryWrapper<>();
        Page<User> page = userService.page(new Page<>(1, 3), queryWrapper);
        String redis_key = String.format("admindemo:user:recommends:%s",mainUserList);
        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        try {
            stringObjectValueOperations.set(redis_key,page,30000, TimeUnit.MILLISECONDS);

        }catch (Exception e){
            log.error("redis set key error",e);
        }
    }

    /**
     * 将doCacheRecommendUser改写成分布式锁的形式
     * redisson 看门狗机制
     * 开一个监听线程，如果方法还没执行完，就帮你重置 redis 锁的过期时间。
     */
    @Scheduled(cron = "0 12 1 * * *")
    public void doCacheRecommendUser1(){
        // 拿到分布式锁
        RLock lock = redissonClient.getLock("admindemo:precache:docache:lock");
        try {
            // 只有一个线程能捕获到锁
            if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("get lock: " + Thread.currentThread().getId());
                for (long id : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    // 查数据库
                    Page<User> page = userService.page(new Page<>(1, 3), queryWrapper);
                    String redis_key = String.format("admindemo:user:recommends:%s",mainUserList);
                    ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
                    // 写缓存 30s过期
                    try {
                        stringObjectValueOperations.set(redis_key,page,30000,TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        log.error("set redis key error", e);
                    }
                }
            }
        }catch (Exception e){
            log.error("doCacheRecommend User error", e);
        }finally {
            // 释放锁
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
