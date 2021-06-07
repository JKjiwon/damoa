package hello.sns.service;

import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.business.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileInfo uploadImage(MultipartFile file) throws FileUploadException;

    List<PostImageInfo> uploadPostImages(List<MultipartFile> files) throws FileUploadException;

    void deleteFile(String filePath);
}
