package com.damoa.service;

import com.damoa.web.dto.common.FileInfo;
import com.damoa.web.dto.post.PostImageInfo;
import com.damoa.web.exception.business.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileInfo uploadImage(MultipartFile file) throws FileUploadException;

    List<PostImageInfo> uploadPostImages(List<MultipartFile> files) throws FileUploadException;

    void deleteFile(String filePath);
}
