package com.aiyichen.admindemo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
@Slf4j
public class RedissonConfig {
    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        System.out.println(redisAddress);
        log.info(redisAddress);
        //  使用单个Redis，没有开集群 useClusterServers()  设置地址和使用库
        //  这里千万要记得添加上密码 不然连不上远程redis
        config.useSingleServer()
                .setAddress("redis://122.51.116.77:6379")
                .setPassword("123456")
                .setDatabase(3);
//        config.useSingleServer();
        // 2. 创建实例

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
