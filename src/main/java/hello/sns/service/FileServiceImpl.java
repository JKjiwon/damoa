package hello.sns.service;

import hello.sns.util.FileUtil;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.post.PostImageInfo;
import hello.sns.web.exception.business.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Value("${sns.upload.path}")
    private String baseDir;

    @Override
    public FileInfo uploadMemberImage(MultipartFile file, Long memberId) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file, "members", memberId);
    }

    @Override
    public FileInfo uploadCommunityImage(MultipartFile file, Long communityId) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file, "communities", communityId);
    }

    public PostImageInfo uploadPostImage(int postId, FileInfo fileInfo) {
        return FileUtil.toPostImageInfo(postId, fileInfo, 1);
    }

    public List<PostImageInfo> uploadPostImages(int postId, List<FileInfo> fileInfos) {
        return fileInfos.stream()
                .map(fileInfo -> FileUtil.toPostImageInfo(postId, fileInfo, fileInfos.indexOf(fileInfo) + 1))
                .collect(Collectors.toList());
    }

    private List<FileInfo> uploadFiles(List<MultipartFile> files, String type, Long memberId) {
        Map<String, String> newFileNames = FileUtil.changeFileNames(files);
        createDirectory(type, memberId);

        return files.stream()
                .map(file ->
                        createFileInfo(file, type, memberId, newFileNames.get(file.getOriginalFilename())))
                .collect(Collectors.toList());
    }

    private FileInfo uploadFile(MultipartFile file, String type, Long entityId) throws FileUploadException {
        String newFileName = FileUtil.changeFileName(file);
        createDirectory(type, entityId);
        return createFileInfo(file, type, entityId, newFileName);
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null) {
            new File(filePath).delete();
        }
    }

    private void createDirectory(String type, Long entityId) {

        StringBuilder dirPath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(type)
                .append(File.separator)
                .append(entityId);

        File directory = new File(String.valueOf(dirPath));

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private FileInfo createFileInfo(MultipartFile file, String type, Long entityId, String newFileName) {

        StringBuilder filePath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(type)
                .append(File.separator)
                .append(entityId)
                .append(File.separator)
                .append(newFileName);
        try {
            file.transferTo(new File(String.valueOf(filePath)));
            return new FileInfo(newFileName, String.valueOf(filePath));
        } catch (IOException e) {
            throw new FileUploadException("Fail to create file", e);
        }
    }

    private void checkImageFile(MultipartFile file) {
        boolean isImage = false;
        try {
            isImage = file.getContentType().startsWith("image");
        } catch (NullPointerException e) {
            throw new FileUploadException(("Not exists an image"));
        }
        if (!isImage) {
            throw new FileUploadException(("Not an image file"));
        }
    }

}
