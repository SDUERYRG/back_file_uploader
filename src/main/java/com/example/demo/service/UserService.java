package com.example.demo.service;


import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
@Service
public  interface UserService extends IService<User>{
    
    public User userAuthentication(User user);
}