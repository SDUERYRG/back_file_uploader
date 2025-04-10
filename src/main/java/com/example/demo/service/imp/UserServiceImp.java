package com.example.demo.service.imp;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@Service
public class UserServiceImp extends ServiceImpl<UserDao, User> implements UserService{

    @Resource
    private UserDao userDao;

    @Override
    public User userAuthentication(User user){
        return userDao.userAuthentication(user);
    }
}
