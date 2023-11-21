package com.aiyichen.admindemo.service.impl;

import com.aiyichen.admindemo.exception.BusinessException;
import com.aiyichen.admindemo.utils.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiyichen.admindemo.entity.User;
import com.aiyichen.admindemo.service.UserService;
import com.aiyichen.admindemo.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.aiyichen.admindemo.constant.UserConstant.ADMIN_ROLE;
import static com.aiyichen.admindemo.constant.UserConstant.USER_LOGIN_STATE;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

//    private static final Type TAG_SET_TYPE = new TypeToken<Set<String>>() {}.getType();
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
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度小于4");
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

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        QueryWrapper wrapper = new QueryWrapper<>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 拼接
        // 遍历list
        for(String tag:tagNameList){
            wrapper = wrapper.like("tags",tag);
        }
        List<User> users = userMapper.selectList(wrapper);

        // TODO 这里返回的应该是安全的用户数据
        return users;
    }

    @Override
    public List<User> searchUsersByTagsMem(List<String> tagNameList) {
//        List<User> userlist=new ArrayList<>();
//        for (String string :tagNameList) {
//        	userlist.add(convertFromString(string));
//        }
//        return userlist;
        if(CollectionUtils.isEmpty(tagNameList)) throw new BusinessException((ErrorCode.PARAMS_ERROR));
        // 什么是序列化？
        // json序列化是把javabean对象转换为json格式的东西

        // 策略一： 使用内存查询
        // 首先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);

        // 初始化一个gson
        Gson gson = new Gson();

//        users.stream()
//        将要处理的元素集合看成一种流，流在管道中传输（元素流）
//        users.stream().filter()
//        filter 过滤 以下代码片段使用filter过滤出空字符串
//        userList.stream().filter(string -> string.isEmpty())
        List<User> finalUsers = users.stream().filter(user -> {
//            String tagstr = gson.toJson(user.getTags());
            String tagstr = user.getTags();
            // 注意： 此时取出来的tagstr是json数据
            if (StringUtils.isBlank(tagstr)) return false;
//            tagstr = gson.toJson(tagstr);
            // 这一段啥意思没看明白 将原本的json字符串转化为一个 set集合 每个元素是原来的json字符串
//            Set<String> tempTagNameSet = gson.fromJson(tagstr, new TypeToken<Set<String>>() {
//            }.getType());
//            Set<String> tempTagNameSet = gson.fromJson(tagstr, TAG_SET_TYPE);
//            Set<String> tempTagNameSet =  gson.fromJson(tagstr,new TypeToken<Set<String>>(){}.getType());
            Set<String> tempTagNameSet =  gson.fromJson(tagstr,new TypeToken<Set<String>>(){}.getType());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        //就是users里面的每一个用户里面的tag字段
// 如果包含了每一个我要求的tag 就返回true 不过滤 否则就过滤掉
        return finalUsers;
    }

    @Override
    public List<User> searchUsersByTagDatasourse(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接tag
        for (String tag : tagNameList){
            queryWrapper = queryWrapper.like("tags",tag);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        return users;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 从request中获取到当前登录的用户的信息
        if (request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            // session里面没有保存用户信息
            // 这个错误码好像不太对 TODO
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        // 先鉴权
        // 管理员能修改任意用户的信息
        // 非管理员只能修改自己的信息
        Long id = user.getId();
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (!(isAdmin(loginUser))&&loginUser.getId()!=user.getId()){
            // 既不是管理员 id
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 有权限 可以修改 先看数据库里有没有对应的数据
        User user1 = userMapper.selectById(id);
        if(user1 == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        return userMapper.updateById(user);
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return (loginUser != null && loginUser.getRole()==ADMIN_ROLE);
    }
}




