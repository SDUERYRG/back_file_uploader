package com.example.demo.service;

import com.example.demo.entity.File;
import java.util.List;
import java.util.Map;

public interface CollectService {
    /**
     * 获取用户收藏的文件
     * @param userId 用户ID
     * @return 收藏的文件列表
     */
    List<File> getCollectedFiles(Integer userId);

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 操作结果消息
     */
    Map<String, Object> addCollect(Integer userId, Integer fileId);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 操作结果消息
     */
    Map<String, Object> removeCollect(Integer userId, Integer fileId);

    /**
     * 检查收藏状态
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 是否已收藏
     */
    boolean isCollected(Integer userId, Integer fileId);
}