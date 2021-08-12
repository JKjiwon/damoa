package com.damoa.service;

import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.dto.post.PostUploadImage;
import com.damoa.web.exception.business.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    UploadFile storeImage(MultipartFile file) throws FileUploadException;

    List<PostUploadImage> storePostImages(List<MultipartFile> files) throws FileUploadException;

    void deleteFile(String filePath);
}
