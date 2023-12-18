package com.aiyichen.admindemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
// 扫描对应的mapper文件
// 为什么只有mapper文件需要@MapperScan进行扫描
@MapperScan("com.aiyichen.admindemo.mapper")
@EnableScheduling
public class AdminDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminDemoApplication.class, args);
    }

}
