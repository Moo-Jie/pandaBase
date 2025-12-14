package com.rich.pandabaseserver.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务
 *
 * @author DuRuiChi
 */
public interface LocalFileStorageService {

    /**
     * 存储文件
     *
     * @param file 上传的文件
     * @return 文件的相对路径
     * @throws IOException IO异常
     */
    String storeFile(MultipartFile file) throws IOException;

    /**
     * 从URL下载并存储文件
     *
     * @param url 文件URL
     * @param fileName 文件名
     * @return 文件的相对路径
     * @throws IOException IO异常
     */
    String storeFileFromUrl(String url, String fileName) throws IOException;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
}

