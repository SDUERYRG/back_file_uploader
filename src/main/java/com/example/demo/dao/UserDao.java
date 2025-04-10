package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;

@Repository
@Mapper
public interface UserDao extends BaseMapper<User>{
    @Select("SELECT * FROM user where username = #{username}")
    User userAuthentication(User user);
}
