package com.aiyichen.admindemo.service;

import com.aiyichen.admindemo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */

// service用单元测试 controller用swagger测试 controller调用mapper mapper调用service
public interface UserService extends IService<User> {

    long userRegister(String account, String password, String checkPassword);
    User userDoLogin(String account, String password, HttpServletRequest request);

    int userLogout(HttpServletRequest request);
    List<User> searchUsersByTags(List<String> tagNameList);
    List<User> searchUsersByTagsMem(List<String> tagNameList);
    List<User> searchUsersByTagDatasourse(List<String> tagNameList);
    User getLoginUser(HttpServletRequest request);
    int updateUser(User user,User loginUser);
    boolean isAdmin(User loginUser);
}
