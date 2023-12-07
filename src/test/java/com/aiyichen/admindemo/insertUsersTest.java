package com.aiyichen.admindemo;
import java.util.Date;

import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class insertUsersTest {
    @Resource
    private UserService userService;
    // 批量插入数据

    // 批量插入数据的方案一 最笨的方法 没有任何优化 就是硬插 每插入一次数据就要创建和销毁一次数据库连接
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User>userList = new ArrayList<>();
        for(int i=0;i<INSERT_NUM;i++){
            User user = new User();
//            user.setId(0L);
            user.setUserName("假kj");
            user.setUserAccount("kjkjkjkj");
            user.setAvatarUrl("https://th.bing.com/th/id/OIP.jfY4nQPLuqb-gEi62_zobgAAAA?rs=1&pid=ImgDetMain");
            user.setGender((byte)0);
            user.setUserPassword("12345678");
            user.setEmail("123");
            user.setUserState(0);
            user.setPhone("123");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDeleted((byte)0);
            user.setRole(0);
            user.setTags("['caonima','caotama']");
            user.setProfile("jiafake");
//            userList.add(user);
            userService.save(user);
        }
        // userList里面有1000条数据
//        userService.saveBatch(userList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }

    // 较好较简单的方法 使用batch批量插入 每次创建一个数据库连接以后插入多个数据
    @Test
    public void doInsertUsersByBatch(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User>userList = new ArrayList<>();
        for (int i=0;i<INSERT_NUM;i++){
            User user = new User();
            user.setUserName("假kj");
            user.setUserAccount("kjkjkjkj");
            user.setAvatarUrl("https://th.bing.com/th/id/OIP.jfY4nQPLuqb-gEi62_zobgAAAA?rs=1&pid=ImgDetMain");
            user.setGender((byte)0);
            user.setUserPassword("12345678");
            user.setEmail("123");
            user.setUserState(0);
            user.setPhone("123");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDeleted((byte)0);
            user.setRole(0);
            user.setTags("['caonima','caotama']");
            user.setProfile("jiafake");
            userList.add(user);
        }
        userService.saveBatch(userList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


    /**
     * 问题有点多 不看
     */
//    @Test
//    public void doInsertUsersMultiThread1(){
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 100000;
//        List<User>userList = new ArrayList<>();
//        int j = 0;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for(int i=0;i<10;i++){
//            userList.clear();
//
//            /**
//             * 在这里使用多线程
//             */
//
//            // 这里定义了一个异步任务
//
//            /**
//             * 注：这里如果要把添加uset进list的操作放进异步任务里面 list集合应该转换成线程安全的东西
//             *
//             */
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                while (true){
//                    j++;
//                    User user = new User();
//                    user.setUserName("假kj");
//                    user.setUserAccount("kjkjkjkj");
//                    user.setAvatarUrl("https://th.bing.com/th/id/OIP.jfY4nQPLuqb-gEi62_zobgAAAA?rs=1&pid=ImgDetMain");
//                    user.setGender((byte)0);
//                    user.setUserPassword("12345678");
//                    user.setEmail("123");
//                    user.setUserState(0);
//                    user.setPhone("123");
//                    user.setCreateTime(new Date());
//                    user.setUpdateTime(new Date());
//                    user.setIsDeleted((byte)0);
//                    user.setRole(0);
//                    user.setTags("['caonima','caotama']");
//                    user.setProfile("jiafake");
//                    userList.add(user);
//                    if(j%10000 == 0) break;
//                }
//                userService.saveBatch(userList, 10000);
//                //
//            });
//            futureList.add(future);
//        }
//
//        // 拿到了十个异步任务 最后执行：
//        // TODO 完全没看懂 为什么要用join 以及前面是怎么写的 还有为什么在这里会阻塞 而不是直接执行stopWatch.stop()
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }

    /**
     * 最牛的方法 使用多线程实现 每次使用多个线程同时使用saveBatch
     * 插入10w条数据 单次使用saveBatch插入1w条
     * 使用多线程
     */
    @Test
    public void doInsertUsersMultiThread(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User>userList = new ArrayList<>();
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for(int i=0;i<10;i++){
            userList.clear();
            while (true){

                j++;
                User user = new User();
                user.setUserName("假kj");
                user.setUserAccount("kjkjkjkj");
                user.setAvatarUrl("https://th.bing.com/th/id/OIP.jfY4nQPLuqb-gEi62_zobgAAAA?rs=1&pid=ImgDetMain");
                user.setGender((byte)0);
                user.setUserPassword("12345678");
                user.setEmail("123");
                user.setUserState(0);
                user.setPhone("123");
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                user.setIsDeleted((byte)0);
                user.setRole(0);
                user.setTags("['caonima','caotama']");
                user.setProfile("jiafake");
                userList.add(user);
                if(j%10000 == 0) break;
            }
            /**
             * 在这里使用多线程
             */

            // 这里定义了一个异步任务

            // CompletableFuture.runAsync接受一个Runnable对象作为参数
            // CompletableFuture.runAsync用于异步地执行指定的runnable代码块
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                userService.saveBatch(userList, 10000);
                //
            });
            futureList.add(future);
        }

        // 拿到了十个异步任务 最后执行：
        // TODO 完全没看懂 为什么要用join 以及前面是怎么写的 还有为什么在这里会阻塞 而不是直接执行stopWatch.stop()

        // 等待一组异步任务执行完成 因为下面要统计时间了 肯定要等插入完成之后再统计时间
        // futureList是一个 List<CompletableFuture<Void>> 包含了一组异步任务CompletableFuture<Void>
        // toArray方法将List转为数组
        // new CompletableFuture[]{} 创建了一个空的CompletableFuture数组 相当于把List转成的数组存到了空数组里面
        // CompletableFuture.allOf 接受一组 CompletableFuture 对象 返回一个新的CompletableFuture
        // 它在所有输入的 CompletableFuture 完成时也完成。换句话说，它等待所有的异步任务都完成。
        // join()方法join 方法是等待 CompletableFuture 的完成
        // 调用 join() 方法就是等待这个新的 CompletableFuture 完成。具体来说，在这个例子中，CompletableFuture.allOf(...) 确保了在所有的异步任务完成之前，join() 不会返回，从而达到等待所有异步任务执行完毕的效果。
        // 通透！
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
