package com.rich.pandabaseserver.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.service.LocalFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件存储服务实现
 *
 * @author DuRuiChi
 */
@Slf4j
@Service
public class LocalFileStorageServiceImpl implements LocalFileStorageService {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.upload.max-size:10}")
    private long maxSize;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,webp}")
    private String allowedTypes;

    /**
     * 存储文件
     *
     * @param file 上传的文件
     * @return 文件的相对路径
     * @throws IOException IO异常
     */
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 检查文件大小（转换为字节）
        long maxSizeBytes = maxSize * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                String.format("文件大小不能超过%dMB", maxSize));
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);

        // 检查文件类型
        if (StrUtil.isNotBlank(allowedTypes) && !allowedTypes.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                "不支持的文件类型，仅支持：" + allowedTypes);
        }

        // 生成文件名（使用日期 + UUID）
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = dateStr + "_" + IdUtil.simpleUUID() + "." + extension;

        // 构建文件存储路径（按日期分目录）
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = uploadPath + datePath + "/" + fileName;

        // 创建目录
        Path path = Paths.get(relativePath);
        Files.createDirectories(path.getParent());

        // 保存文件
        file.transferTo(path.toFile());
        
        log.info("文件上传成功: {}", relativePath);
        return relativePath;
    }

    /**
     * 从URL下载并存储文件
     *
     * @param url 文件URL
     * @param fileName 文件名（可选，为null时自动生成）
     * @return 文件的相对路径
     * @throws IOException IO异常
     */
    @Override
    public String storeFileFromUrl(String url, String fileName) throws IOException {
        if (StrUtil.isBlank(url)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "URL不能为空");
        }

        try {
            // 如果没有提供文件名，从URL中获取
            if (StrUtil.isBlank(fileName)) {
                fileName = FileUtil.getName(url);
            }

            // 获取文件扩展名
            String extension = FileUtil.extName(fileName);
            if (StrUtil.isBlank(extension)) {
                extension = "jpg"; // 默认扩展名
            }

            // 生成文件名
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String newFileName = dateStr + "_" + IdUtil.simpleUUID() + "." + extension;

            // 构建文件存储路径
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = uploadPath + datePath + "/" + newFileName;

            // 创建目录
            Path path = Paths.get(relativePath);
            Files.createDirectories(path.getParent());

            // 下载文件
            HttpUtil.downloadFile(url, path.toFile());

            log.info("从URL下载文件成功: {} -> {}", url, relativePath);
            return relativePath;

        } catch (Exception e) {
            log.error("从URL下载文件失败: {}", url, e);
            throw new IOException("下载文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return false;
        }

        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("文件删除成功: {}", filePath);
                }
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            return false;
        }
    }
}

