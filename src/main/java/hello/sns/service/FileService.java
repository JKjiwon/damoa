package hello.sns.service;

import hello.sns.web.dto.common.FileDto;
import hello.sns.web.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    FileDto uploadFile(MultipartFile file, Long memberId) throws FileUploadException;

    List<FileDto> uploadFiles(List<MultipartFile> files, Long memberId) throws FileUploadException;

    void deleteFile(String filePath);

}
