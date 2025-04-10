package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        // 这里应添加根据用户名和密码查询数据库的逻辑
        // 示例中简单返回登录结果
        if ("test".equals(user.getUsername()) && "test".equals(user.getPassword())) {
            return "登录成功";
        } else {
            return "登录失败";
        }
    }
    @GetMapping("/hello")
    public User hello() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        return userService.userAuthentication(user);
    }
}