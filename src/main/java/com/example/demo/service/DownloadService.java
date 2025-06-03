package com.example.demo.service;

import com.example.demo.entity.File;
import java.util.List;
import java.util.Map;

public interface DownloadService {
    /**
     * 通过用户ID获取下载的文件列表
     * @param userId 用户ID
     * @return 下载的文件列表
     */
    List<File> getDownloadFilesByUserId(Integer userId);

    /**
     * 记录文件下载
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 操作结果
     */
    Map<String, Object> recordDownload(Integer userId, Integer fileId);
} 