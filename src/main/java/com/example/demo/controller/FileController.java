package com.example.demo.controller;

import com.example.demo.service.FileService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;
    /**
     * 文件上传接口
     * @return 上传结果信息
     * @author wyc
     * @throws IOException 
     * @Date 5/17
     **/
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId, @RequestParam("fileId") int fileId) throws IOException {
        System.out.println("文件名称：" + file.getOriginalFilename());
        System.out.println("文件类型：" + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1));
        System.out.println("文件大小：" + file.getSize());
        System.out.println("用户ID：" + userId);
        fileService.uploadFile(file, userId);
        return "文件上传成功";
    }

    /**
     * 获取文件数量接口
     * @return 文件数量
     * @author wyc
     * @Date 5/17
     **/
    @GetMapping("/getFileCount")
    public int getFileCount() {
        return fileService.getFileCount();
    }
}
