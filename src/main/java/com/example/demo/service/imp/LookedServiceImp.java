package com.example.demo.service.imp;

import com.example.demo.dao.FileDao;
import com.example.demo.dao.LookedDao;
import com.example.demo.entity.Looked;
import com.example.demo.service.LookedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LookedServiceImp implements LookedService {

    private final LookedDao lookedDao;
    private final FileDao fileDao;

    @Autowired
    public LookedServiceImp(LookedDao lookedDao, FileDao fileDao) {
        this.lookedDao = lookedDao;
        this.fileDao = fileDao;
    }

    @Override
    @Transactional
    public boolean addLookedRecord(Integer userId, Integer fileId) {
        // 1. 更新文件浏览量
        int updateCount = fileDao.increaseLookedNum(fileId);
        if (updateCount <= 0) {
            return false; // 文件不存在或更新失败
        }

        // 2. 创建浏览记录
        Looked looked = new Looked();
        looked.setUserid(userId);
        looked.setFileid(fileId);

        // 3. 插入浏览记录
        return lookedDao.insertLooked(looked) > 0;
    }

    @Override
    public List<Looked> getLookedRecords(Integer userId) {
        return lookedDao.findLookedByUserId(userId);
    }
}