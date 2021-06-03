package hello.sns.service;

import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileInfo uploadImageFile(MultipartFile file, Long memberId) throws FileUploadException;

    void deleteFile(String filePath);
}
