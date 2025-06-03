package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("looked")
@Data
public class Looked {
    @TableId("id")
    private Integer id;
    private Integer userid;
    private Integer fileid;
    private Date time;
}
