package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.File;

@Service
public interface FileService extends IService<File>{
    public boolean uploadFile(MultipartFile file, String userId) throws IOException;

    public int getFileCount();

    List<File> getFilesByCategory(String category);

    List<Map<String, Object>> getFileCategories();

    List<File> getTopViewedFiles();

    /**
     * 获取用户的活跃文件列表（未删除的文件）
     * @param userId 用户ID
     * @return 文件列表
     */
    List<File> getActiveFilesByUserId(Integer userId);

    Map<String, Object> logicDeleteFileByUserId(Integer userId, Integer fileId);
}
