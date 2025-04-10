package com.example.prj.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // 配置上传目录，默认为当前目录下的uploads文件夹
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path fileStoragePath;

    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建文件上传目录", ex);
        }
    }

    /**
     * 保存文件并返回文件名
     */
    public String storeFile(MultipartFile file) {
        // 获取原始文件名并清理
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 检查文件名是否包含无效字符
        if (originalFileName.contains("..")) {
            throw new RuntimeException("文件名包含无效路径序列: " + originalFileName);
        }

        // 生成唯一文件名
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 复制文件到目标位置
            Path targetLocation = this.fileStoragePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("无法存储文件 " + originalFileName, ex);
        }
    }

    /**
     * 加载文件为资源
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStoragePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("文件不存在: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("文件不存在: " + fileName, ex);
        }
    }
    
    /**
     * 获取文件的相对路径（用于前端访问）
     */
    public String getFileUrl(String fileName) {
        return "/uploads/" + fileName;
    }
} 