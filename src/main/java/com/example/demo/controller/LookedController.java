package com.example.demo.controller;

import com.example.demo.entity.Looked;
import com.example.demo.service.LookedService;
import com.example.demo.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/looked")
public class LookedController {

    private final LookedService lookedService;

    @Autowired
    public LookedController(LookedService lookedService) {
        this.lookedService = lookedService;
    }

    /**
     * 添加浏览记录
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result addLookedRecord(@RequestParam("userId") Integer userId,
                                  @RequestParam("fileId") Integer fileId) {
        try {
            boolean success = lookedService.addLookedRecord(userId, fileId);
            if (success) {
                return Result.success(200, "浏览记录添加成功");
            } else {
                return Result.error(500, "浏览记录添加失败");
            }
        } catch (Exception e) {
            return Result.error(500, "添加浏览记录异常: " + e.getMessage());
        }
    }

    /**
     * 获取用户浏览记录
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    @GetMapping("/user/{userId}")
    public Result getLookedRecords(@PathVariable("userId") Integer userId) {
        System.out.println("开始获取浏览记录");
        try {
            List<Looked> records = lookedService.getLookedRecords(userId);
            System.out.println("获取到的浏览记录信息: " + records);
            return Result.success(200, "获取浏览记录成功", records);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取浏览记录失败: " + e.getMessage());
        }
    }
}