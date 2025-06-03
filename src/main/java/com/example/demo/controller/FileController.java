package com.example.demo.controller;

import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.service.DownloadService;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file") // 添加统一路径前缀
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;

    @Autowired
    private DownloadService downloadService;
    /**
     * 文件上传接口
     * @return 上传结果信息
     * @author wyc
     * @throws IOException 
     * @Date 5/17
     **/
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId, @RequestParam(value = "fileId", required = false, defaultValue = "0") int fileId) throws IOException {
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

    @GetMapping("/categories")
    public Map<String, Object> getFileCategories() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> categories = fileService.getFileCategories();
            result.put("code", 200);
            result.put("data", categories);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取分类失败");
        }
        return result;
    }

    @GetMapping("/topViewed")
    public Map<String, Object> getTopViewedFiles() {
        System.out.println("开始获取top10数据");
        Map<String, Object> result = new HashMap<>();
        try {
            List<File> files = fileService.getTopViewedFiles();
            result.put("code", 200);
            result.put("data", files);
            System.out.println("获取成功"+files);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "查询失败");
        }
        return result;
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) throws IOException {
        File file = fileService.getById(id);
        
        // 直接使用数据库中存储的绝对路径
        Path filePath = Paths.get(file.getFilepath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, getAccurateMimeType(filePath))
                .header("Content-Disposition", "inline; filename=\"" + file.getFilename() + "\"")
                .header("Access-Control-Expose-Headers", "Content-Length, Content-Range")
                .header("Content-Security-Policy", "frame-ancestors 'self' https://view.officeapps.live.com")
                .header("X-Frame-Options", "ALLOW-FROM https://view.officeapps.live.com")
                .body(new PathResource(filePath));
    }

    private String getAccurateMimeType(Path filePath) throws IOException {
        // 优先使用系统检测
        String detectedType = Files.probeContentType(filePath);
        if (detectedType != null) return detectedType;

        // 扩展自定义类型映射
        String fileName = filePath.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".doc")) return "application/msword";
        if (fileName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (fileName.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (fileName.endsWith(".pptx")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".txt")) return "text/plain";

        return "application/octet-stream"; // 默认类型
    }
    

    @GetMapping("/byCategory")
    public Map<String, Object> getFilesByCategory(@RequestParam String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<File> files = fileService.getFilesByCategory(category);
            result.put("code", 200);
            result.put("data", files);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "查询失败");
        }
        return result;
    }

    /**
     * 获取用户的文件列表（仅显示未删除的文件）
     */
    @GetMapping("/byUser")
    public Map<String, Object> getFilesByUserId(
            @RequestParam Integer userId) {

        Map<String, Object> result = new HashMap<>();
        try {
            List<File> files = fileService.getActiveFilesByUserId(userId);
            result.put("code", 200);
            result.put("data", files);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取文件失败: " + e.getMessage());
        }
        return result;
    }

    

    @GetMapping("/getFileDetail")
    public Map<String, Object> getFileDetail(@RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            File file = fileService.getById(id);
            if (file == null) {
                result.put("code", 404);
                result.put("msg", "文件不存在");
                return result;
            }

            // 获取上传者信息
            String uploader = "未知";
            if (file.getUserid() != null) {  // 修改为 getUserid()
                User user = userService.getById(file.getUserid());
                if (user != null) {
                    uploader = user.getName();
                }
            }

            // 构建返回数据
            Map<String, Object> fileData = new HashMap<>();
            fileData.put("filename", file.getFilename());
            fileData.put("filetype", file.getFiletype());
            fileData.put("uploader", uploader);
            fileData.put("lookednum", file.getLookednum());
            fileData.put("collectnum", file.getCollectnum());
            fileData.put("downloadnum", file.getDownloadnum());

            result.put("code", 200);
            result.put("data", fileData);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取文件详情失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id, @RequestParam(required = false) Integer userId) {
        File file = fileService.getById(id);
        if (file == null || "deleted".equals(file.getState())) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 直接使用数据库中存储的绝对路径
            Path filePath = Paths.get(file.getFilepath());
            
            System.out.println("尝试下载文件: " + filePath);
            System.out.println("文件是否存在: " + Files.exists(filePath));

            if (userId != null) {
                downloadService.recordDownload(userId, id);
            }

            Resource resource = new PathResource(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, getMimeType(file.getFiletype()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            System.err.println("文件下载失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    private String getMimeType(String fileType) {
        switch (fileType.toLowerCase()) {
            case "pdf": return "application/pdf";
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "txt": return "text/plain";
            default: return "application/octet-stream";
        }
    }

    // 用户逻辑删除自己上传的文件
    @PutMapping("/logicDelete")
    public ResponseEntity<?> logicDeleteFile(
            @RequestParam Integer userId,
            @RequestParam Integer fileId) {

        try {
            Map<String, Object> result = fileService.logicDeleteFileByUserId(userId, fileId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", "failed");
            errorMap.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorMap);
        } catch (RuntimeException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", "error");
            errorMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
}
