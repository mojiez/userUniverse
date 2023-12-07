package com.aiyichen.admindemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//@SpringBootTest
public class MutiTreadTest {
    @Test
    public void testMutiThread(){
        // 一个runnable实例通常会由一个线程处理 也就是说
        // Runnable内部的代码是顺序执行的！！！！
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for(int j=0;j<5;j++){
            Runnable myRunnable = () -> {
                for (int i = 0; i < 2; i++) {
                    System.out.println(Thread.currentThread().getName() + ": " + i);
                }
            };
            CompletableFuture<Void> future = CompletableFuture.runAsync(myRunnable);
            futureList.add(future);
        }
        // 使用 CompletableFuture.runAsync 异步执行 Runnable


        // 在这里，你可以执行其他操作，而不必等待异步任务完成

        // 使用 join 等待异步任务完成
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        return;
    }

    @Test
    public void getMainThreadName(){
        System.out.println("Main thread name: " + Thread.currentThread().getName());
        Runnable myrunnable = () -> {
            for(int i=0;i<2;i++){
//                Thread.currentThread().getName()
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        };
        CompletableFuture<Void> future = CompletableFuture.runAsync(myrunnable);
        future.join();
    }
}
