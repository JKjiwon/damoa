package hello.sns.service;

import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.business.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    FileInfo uploadImage(MultipartFile file) throws FileUploadException;

    PostImageInfo uploadPostImage(int postId, MultipartFile file) throws FileUploadException;

    public List<PostImageInfo> uploadPostImages(int postId, List<MultipartFile> files) throws FileUploadException;

    void deleteFile(String filePath);
}
