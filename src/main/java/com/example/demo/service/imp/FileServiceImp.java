package com.example.demo.service.imp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    private static final Map<String, List<String>> CATEGORY_MAPPING = new HashMap<>();
    static {
        CATEGORY_MAPPING.put("图片", Arrays.asList("jpg", "jpeg", "png"));
        CATEGORY_MAPPING.put("视频", Arrays.asList("mp4", "mov", "avi","wmv"));
        CATEGORY_MAPPING.put("文档", Arrays.asList("doc", "docx", "pdf","xls","xlsx"));
        CATEGORY_MAPPING.put("PPT", Arrays.asList("ppt", "pptx"));
        CATEGORY_MAPPING.put("压缩包", Arrays.asList("zip", "tar","rar","7z","gz"));
        CATEGORY_MAPPING.put("其他", Collections.emptyList());
    }

    
    @Override
    public List<com.example.demo.entity.File> getFilesByCategory(String category) {
        List<String> targetTypes = CATEGORY_MAPPING.getOrDefault(category, Collections.emptyList());
        System.out.println("[DEBUG] 分类名称: " + category + ", 匹配类型: " + targetTypes);

        if ("其他".equals(category)) {
            // 获取所有预定义分类的类型列表（如 ["doc", "docx", "pdf", ...]）
            List<String> allDefinedTypes = CATEGORY_MAPPING.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (allDefinedTypes.isEmpty()) {
                // 如果没有预定义分类，直接返回所有文件
                return baseMapper.selectList(null);
            }

            // 将列表转换为 'doc','docx','pdf' 格式
            String typesStr = "'" + String.join("','", allDefinedTypes) + "'";
            System.out.println("[DEBUG] 其他分类的SQL条件: filetype NOT IN (" + typesStr + ")");
            return baseMapper.findByFileTypeNotIn(typesStr);
        }

        if (targetTypes.isEmpty()) {
            return Collections.emptyList();
        }

        // 处理普通分类
        String typesStr = "'" + String.join("','", targetTypes) + "'";
        System.out.println("[DEBUG] 普通分类的SQL条件: filetype IN (" + typesStr + ")");
        return baseMapper.findByFileTypeIn(typesStr);
    }

    @Override
    public List<Map<String, Object>> getFileCategories() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<String> existingTypes = baseMapper.findDistinctFileTypes().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        System.out.println("[DEBUG] 有效文件类型: " + existingTypes);

        CATEGORY_MAPPING.forEach((category, exts) -> {
            List<String> matchTypes = existingTypes.stream()
                    .filter(exts::contains)
                    .collect(Collectors.toList());
            System.out.println("[DEBUG] 分类匹配结果: " + category + " -> " + matchTypes);

            // 生成带引号的SQL参数（如 'pdf','docx'）
            String typesStr = "'" + String.join("','", matchTypes) + "'";
            Long count = matchTypes.isEmpty() ? 0L : baseMapper.countByTypes(typesStr);

            // 强制添加所有分类，即使count为0
            Map<String, Object> item = new HashMap<>();
            item.put("name", category);
            item.put("icon", "/icons/" + category + ".svg");
            item.put("count", count);
            result.add(item);  // 关键点：不跳过空分类
        });

        System.out.println("[DEBUG] 最终返回的分类数据: " + result);  // 添加日志确认数据
        return result;
    }

    @Override
    public List<com.example.demo.entity.File> getTopViewedFiles() {
        return fileDao.findTop10ByOrderByLookednumDesc();
    }

    @Override
    public List<com.example.demo.entity.File> getActiveFilesByUserId(Integer userId) {
        return fileDao.findActiveFilesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> logicDeleteFileByUserId(Integer userId, Integer fileId) {
        Map<String, Object> resultMap = new HashMap<>();

        // 1. 检查文件是否存在
        com.example.demo.entity.File file = fileDao.selectById(fileId);
        if (file == null) {
            resultMap.put("status", "failed");
            resultMap.put("message", "文件不存在");
            return resultMap;
        }

        // 2. 权限验证（必须为文件所有者）
        if (!file.getUserid().equals(userId)) {
            resultMap.put("status", "failed");
            resultMap.put("message", "无权删除他人文件");
            return resultMap;
        }

        // 3. 检查文件是否已经被删除
        if ("deleted".equals(file.getState())) {
            resultMap.put("status", "failed");
            resultMap.put("message", "文件已被删除");
            return resultMap;
        }

        // 4. 逻辑删除（更新state字段）
        int updateResult = fileDao.logicDeleteById(fileId);
        if (updateResult <= 0) {
            resultMap.put("status", "failed");
            resultMap.put("message", "删除失败，请重试");
            return resultMap;
        }

        // 5. 返回成功结果
        resultMap.put("status", "success");
        resultMap.put("message", "文件删除成功");
        Map<String, Object> dataMap2 = new HashMap<>();
        dataMap2.put("deleted", true);
        dataMap2.put("filename", file.getFilename());
        resultMap.put("data", dataMap2);

        return resultMap;
    }
}
