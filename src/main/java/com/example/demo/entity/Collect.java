package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("collect")
@Data
public class Collect {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userid;
    private Integer fileid;
}
