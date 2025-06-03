package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("comment")
@Data
public class Comment {
    @TableId("id")
    private Integer id;
    private Integer userid;
    private Integer fileid;
    private String content;
    private Date time;
}
