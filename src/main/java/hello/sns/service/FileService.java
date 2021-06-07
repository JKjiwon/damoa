package hello.sns.service;

import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.business.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileInfo uploadMemberImage(MultipartFile file, Long memberId) throws FileUploadException;

    FileInfo uploadCommunityImage(MultipartFile file, Long communityId) throws FileUploadException;

    PostImageInfo uploadPostImage(int postId, FileInfo fileInfo) throws FileUploadException;

    List<PostImageInfo> uploadPostImages(int postId, List<FileInfo> fileInfos) throws FileUploadException;

    void deleteFile(String filePath);

}
