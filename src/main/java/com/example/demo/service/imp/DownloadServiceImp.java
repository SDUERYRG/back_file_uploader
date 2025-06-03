package com.example.demo.service.imp;

import com.example.demo.dao.DownloadDao;
import com.example.demo.dao.FileDao;
import com.example.demo.entity.Download;
import com.example.demo.entity.File;
import com.example.demo.service.DownloadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DownloadServiceImp implements DownloadService {

    @Resource
    private DownloadDao downloadDao;

    @Resource
    private FileDao fileDao;

    @Override
    public List<File> getDownloadFilesByUserId(Integer userId) {
        return downloadDao.findFilesByUserId(userId);
    }

    @Override
    @Transactional
    public Map<String, Object> recordDownload(Integer userId, Integer fileId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查文件是否存在且未删除
            File file = fileDao.selectById(fileId);
            if (file == null || "deleted".equals(file.getState())) {
                result.put("code", 404);
                result.put("msg", "文件不存在或已删除");
                return result;
            }

            // 记录下载（允许重复下载）
            Download download = new Download();
            download.setUserid(userId);
            download.setFileid(fileId);
            download.setTime(new Date());
            downloadDao.insertDownload(download);

            // 更新文件下载次数
            fileDao.increaseDownloadNum(fileId);

            result.put("code", 200);
            result.put("msg", "下载记录成功");
            return result;

        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "下载记录失败: " + e.getMessage());
            return result;
        }
    }
} 