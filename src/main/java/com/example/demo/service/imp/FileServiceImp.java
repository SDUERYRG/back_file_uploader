package com.example.demo.service.imp;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.FileDao;

import com.example.demo.service.FileService;

@Service
public class FileServiceImp extends ServiceImpl<FileDao,com.example.demo.entity.File> implements FileService{
    @Resource
    private FileDao fileDao;


    /**
     * 文件上传接口
     * @return 上传结果信息
     * @author wyc
     * @throws IOException
     * @Date 5/17
     **/
    @Override
    public boolean uploadFile(MultipartFile file, String userId) throws IOException {
        try {
            if (file.isEmpty()) {
                return false;
            }
            String fileName = file.getOriginalFilename();
            String basePath = System.getProperty("user.dir") + File.separator + "file";
            String filePath = basePath + File.separator + userId + File.separator + fileName;
            File dest = new File(filePath);

            // 检查目录是否存在，如果不存在则创建
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            com.example.demo.entity.File myFile = new com.example.demo.entity.File();
            myFile.setFilename(fileName);

            // 取file\后面的路径
            String newFilePath = filePath.substring(filePath.indexOf("file\\"));
            myFile.setFilepath(newFilePath);
            myFile.setUserid(Integer.parseInt(userId));
            myFile.setFiletype(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1)); // 假设文件类型为0，需要根据实际情况设置
            myFile.setFilesize((int) file.getSize());
            
            
            
            // 这里调用Dao层方法更新数据库记录
            return fileDao.incrementFileCount(myFile); 
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文件数量接口
     * @return 文件数量
     * @author wyc
     * @Date 5/17
     **/
    public int getFileCount() {
        return fileDao.getFileCount();
    }
}
