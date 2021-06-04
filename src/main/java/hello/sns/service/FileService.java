package hello.sns.service;

import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.exception.business.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileInfo uploadMemberImageFile(MultipartFile file, Long memberId) throws FileUploadException;

    FileInfo uploadCommunityImageFile(MultipartFile file, Long communityId) throws FileUploadException;

    void deleteFile(String filePath);
}
