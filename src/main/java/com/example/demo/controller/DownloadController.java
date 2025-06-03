package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.service.DownloadService;
import com.example.demo.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/download")
@CrossOrigin
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    /**
     * 获取用户的下载记录
     * @param userId 用户ID
     * @return 下载的文件列表
     */
    @GetMapping("/getUserDownloads")
    public Result getUserDownloads(@RequestParam Integer userId) {
        try {
            System.out.println("=== 获取下载记录 ===");
            System.out.println("接收到的用户ID: " + userId);
            
            List<File> files = downloadService.getDownloadFilesByUserId(userId);
            System.out.println("查询到的文件数量: " + files.size());
            System.out.println("查询到的文件列表: " + files);
            
            return Result.success(200, "获取下载记录成功", files);
        } catch (Exception e) {
            System.err.println("获取下载记录失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取下载记录失败: " + e.getMessage());
        }
    }

    /**
     * 记录文件下载
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 操作结果
     */
    @PostMapping("/record")
    public Result recordDownload(@RequestParam Integer userId, @RequestParam Integer fileId) {
        try {
            Map<String, Object> result = downloadService.recordDownload(userId, fileId);
            int code = (Integer) result.get("code");
            String msg = (String) result.get("msg");
            
            if (code == 200) {
                return Result.success(code, msg);
            } else {
                return Result.error(code, msg);
            }
        } catch (Exception e) {
            return Result.error(500, "记录下载失败: " + e.getMessage());
        }
    }
} 