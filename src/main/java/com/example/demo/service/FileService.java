package com.example.demo.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.File;

@Service
public interface FileService extends IService<File>{
    public boolean uploadFile(MultipartFile file, String userId) throws IOException;

    public int getFileCount();
}
