package com.aiyichen.admindemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.service.UserService;
import com.aiyichen.admindemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.aiyichen.admindemo.constant.UserConstant.USER_LOGIN_STATE;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Autowired
    private UserMapper userMapper;
    @Override
    public long userRegister(String account, String password, String checkPassword) {
        if(account.isEmpty()||password.isEmpty()||checkPassword.isEmpty()) {
            return -1;
        }
        if(account.length()<4){
            return -1;
        }
        if(password.length()<8){
            return -1;
        }
        if(!password.equals(checkPassword)){
            return -1;
        }

        // 账户不包含特殊字符？？
        // 如果账号中有1.标点字符\pP 2.符号字符\pS 3.\s+ 一个或多个空白字符 的话 就不能要了
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&* （）——+|{}【】‘；：”“’。，、？]";

        // Pattern.compile 是 Java 中用于编译正则表达式的方法。它接受一个正则表达式字符串作为参数，并返回一个 Pattern 对象，该对象表示编译后的正则表达式，可以用于进行字符串匹配。
        Pattern pattern = Pattern.compile(validPattern);
        // matcher对象用于执行正则表达式的匹配操作
        Matcher matcher = pattern.matcher(account);
        if(matcher.find()){
            return -1;
        }

        // 账户不能重复 在数据库中找有没有相同账户的
//        Wrapper wrapper = new QueryWrapper<User>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account",account);
        Long num = userMapper.selectCount(wrapper);
        if(num>0){
            return -1;
        }

        // 给密码加密 （加密策略可替换 这里是MD5）
        // 如何实现MD5？？
        final String SALT = "yichen";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 插入数据
        User user = new User();
        user.setUserPassword(encryptPassword);
        user.setUserAccount(account);
        int insert = userMapper.insert(user);
        User user1 = userMapper.selectById(user.getId());
        if(insert > 0){
            //插入成功 返回id
            System.out.println(user1.getCreateTime());
            return user.getId();
        }
        return -1;
    }

    @Override
    public User userDoLogin(String account, String password, HttpServletRequest request) {
        // 1.校验
        if(account.isEmpty()||password.isEmpty()){
            return null;
        }
        if(account.length()<4){
            return null;
        }
        if(password.length()<8){
            return null;
        }

        // 不能包含特殊字符
        // 账户不包含特殊字符？？
        // 如果账号中有1.标点字符\pP 2.符号字符\pS 3.\s+ 一个或多个空白字符 的话 就不能要了
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&* （）——+|{}【】‘；：”“’。，、？]";

        // Pattern.compile 是 Java 中用于编译正则表达式的方法。它接受一个正则表达式字符串作为参数，并返回一个 Pattern 对象，该对象表示编译后的正则表达式，可以用于进行字符串匹配。
        Pattern pattern = Pattern.compile(validPattern);
        // matcher对象用于执行正则表达式的匹配操作
        Matcher matcher = pattern.matcher(account);
        if(matcher.find()){
            return null;
        }

        // 密码加密
        final String SALT = "yichen";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 开始查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account",account);
        wrapper.eq("user_password",encryptPassword);
        User user = userMapper.selectOne(wrapper);

        if(user == null){
            return null;
        }

        // 用户脱敏 就是把敏感信息删掉（不敏感的信息存到session里面）
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
//        safetyUser.setUserPassword();
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserState(user.getUserState());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUpdateTime(user.getUpdateTime());
        safetyUser.setRole(user.getRole());
//        safetyUser.setIsDeleted();



        // 记录用户的登陆状态
        // 但是登陆状态要保存下来（保存到服务器端 使用request保存）
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
}




