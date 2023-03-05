package com.damoa.domain.common.service;

import com.damoa.domain.common.dto.UploadFile;
import com.damoa.domain.post.dto.PostUploadImage;
import com.damoa.domain.common.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    UploadFile storeImage(MultipartFile file) throws FileUploadException;

    List<PostUploadImage> storePostImages(List<MultipartFile> files) throws FileUploadException;

    void deleteFile(String filePath);
}