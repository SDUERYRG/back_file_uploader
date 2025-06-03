package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.service.CollectService;
import com.example.demo.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collect")
public class CollectController {

    private final CollectService collectService;

    @Autowired
    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    /**
     * 获取用户收藏的文件
     * @param userId 用户ID
     * @return 收藏的文件列表
     */
    @GetMapping("/user/{userId}")
    public Result getCollectedFiles(@PathVariable("userId") Integer userId) {
        try {
            List<File> files = collectService.getCollectedFiles(userId);
            return Result.success(200, "获取收藏文件成功", files);
        } catch (Exception e) {
            return Result.error(500, "获取收藏文件失败: " + e.getMessage());
        }
    }

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result addCollect(@RequestParam("userId") Integer userId,
                             @RequestParam("fileId") Integer fileId) {
        try {
            // 调用Service方法（返回Map结构）
            Map<String, Object> serviceResult = collectService.addCollect(userId, fileId);

            // 将Service结果封装到Result中
            return Result.success(
                    serviceResult.get("data"),  // 透传isCollected和collectNum
                    serviceResult.get("message").toString()
            );

        } catch (Exception e) {
            return Result.error(500, "收藏失败: " + e.getMessage());
        }
    }

    @PostMapping("/remove")
    public Result removeCollect(@RequestParam("userId") Integer userId,
                                @RequestParam("fileId") Integer fileId) {
        try {
            // 调用Service方法（返回Map结构）
            Map<String, Object> serviceResult = collectService.removeCollect(userId, fileId);
            System.out.println("传回信息："+serviceResult);

            // 将Service结果封装到Result中
            return Result.success(
                    serviceResult.get("data"),  // 透传isCollected和collectNum
                    serviceResult.get("message").toString()
            );

        } catch (Exception e) {
            return Result.error(500, "取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 检查收藏状态
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 是否已收藏
     */
    @GetMapping("/status")
    public Result checkCollectStatus(@RequestParam("userId") Integer userId,
                                     @RequestParam("fileId") Integer fileId) {
        try {
            boolean isCollected = collectService.isCollected(userId, fileId);
            return Result.success(200, "查询成功", isCollected);
        } catch (Exception e) {
            return Result.error(500, "查询失败: " + e.getMessage());
        }
    }
}