package com.example.demo.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.File;

@Repository
@Mapper
public interface FileDao extends BaseMapper<File>{
    @Insert("INSERT INTO file (filename, filepath, userid, filetype, filesize) VALUES (#{filename}, #{filepath}, #{userid}, #{filetype}, #{filesize})")
    public boolean incrementFileCount(File file);

    @Select("SELECT COUNT(*) FROM file")
    public int getFileCount();
}
