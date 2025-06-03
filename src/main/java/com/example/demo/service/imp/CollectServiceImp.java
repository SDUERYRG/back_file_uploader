package com.example.demo.service.imp;

import com.example.demo.dao.CollectDao;
import com.example.demo.dao.FileDao;
import com.example.demo.entity.Collect;
import com.example.demo.entity.File;
import com.example.demo.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectServiceImp implements CollectService {

    private final CollectDao collectDao;
    private final FileDao fileDao;

    @Autowired
    public CollectServiceImp(CollectDao collectDao, FileDao fileDao) {
        this.collectDao = collectDao;
        this.fileDao = fileDao;
    }

    @Override
    public List<File> getCollectedFiles(Integer userId) {
        return collectDao.findFilesByUserId(userId);
    }

    @Override
    @Transactional
    public Map<String, Object> addCollect(Integer userId, Integer fileId) {
        Map<String, Object> resultMap = new HashMap<>();

        // 1. 检查是否已收藏
        if (collectDao.checkCollectExists(userId, fileId) > 0) {
            resultMap.put("status", "failed");
            resultMap.put("message", "您已收藏过此文件");
            return resultMap;
        }

        // 2. 插入收藏记录
        Collect collect = new Collect();
        collect.setUserid(userId);
        collect.setFileid(fileId);
        int insertResult = collectDao.insertCollect(collect);

        if (insertResult <= 0) {
            resultMap.put("status", "failed");
            resultMap.put("message", "收藏失败，请重试");
            return resultMap;
        }

        // 3. 更新文件收藏量
        int updateResult = fileDao.increaseCollectNum(fileId);
        if (updateResult > 0) {
            resultMap.put("status", "success");
            resultMap.put("message", "收藏成功");
            resultMap.put("isCollected", true);
            resultMap.put("collectNum", fileDao.getCollectNum(fileId));
        } else {
            throw new RuntimeException("更新收藏量失败");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "收藏成功");
        Map<String, Object> data = new HashMap<>();
        data.put("isCollected", true);
        data.put("collectnum", fileDao.getCollectNum(fileId));
        result.put("data", data);
        return result;
    }

    // 新增方法：获取文件收藏量
    public int getCollectNum(Integer fileId) {
        return fileDao.getCollectNum(fileId);
    }

    @Override
    @Transactional
    public Map<String, Object> removeCollect(Integer userId, Integer fileId) {
        Map<String, Object> resultMap = new HashMap<>();

        // 1. 检查是否已收藏
        if (collectDao.checkCollectExists(userId, fileId) == 0) {
            resultMap.put("status", "failed");
            resultMap.put("message", "您尚未收藏此文件");
            return resultMap;
        }

        // 2. 删除收藏记录
        int deleteResult = collectDao.deleteCollect(userId, fileId);
        if (deleteResult <= 0) {
            resultMap.put("status", "failed");
            resultMap.put("message", "取消收藏失败，请重试");
            return resultMap;
        }

        // 3. 更新文件收藏量
        int updateResult = fileDao.decreaseCollectNum(fileId);
        if (updateResult > 0) {
            resultMap.put("status", "success");
            resultMap.put("message", "取消收藏成功");
            resultMap.put("isCollected", false);  // 修改这里 - 应该为false
            resultMap.put("collectNum", fileDao.getCollectNum(fileId));
        } else {
            throw new RuntimeException("更新收藏量失败");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "取消收藏成功");
        Map<String, Object> data = new HashMap<>();
        data.put("isCollected", false);  // 修改这里 - 应该为false
        data.put("collectnum", fileDao.getCollectNum(fileId));
        result.put("data", data);
        return result;
    }

    @Override
    public boolean isCollected(Integer userId, Integer fileId) {
        return collectDao.checkCollectExists(userId, fileId) > 0;
    }
}