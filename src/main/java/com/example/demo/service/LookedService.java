package com.example.demo.service;

import com.example.demo.entity.Looked;
import java.util.List;

public interface LookedService {
    /**
     * 添加浏览记录并更新文件浏览量
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 是否成功
     */
    boolean addLookedRecord(Integer userId, Integer fileId);

    /**
     * 获取用户浏览记录
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    List<Looked> getLookedRecords(Integer userId);
}