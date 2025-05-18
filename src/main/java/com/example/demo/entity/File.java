package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("file")
@Data
public class File {
    @TableId("id")
    private Integer id;
    private String filename;
    private String filepath;
    private Integer userid;
    private String filetype;
    private Integer filesize;
}
