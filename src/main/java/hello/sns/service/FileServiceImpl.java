package hello.sns.service;

import hello.sns.util.FileUtil;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {

    @Value("${sns.upload.path}")
    private String baseDir;

    public FileInfo uploadMemberImageFile(MultipartFile file, Long memberId) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file, "members", memberId);
    }

    @Override
    public FileInfo uploadCommunityImageFile(MultipartFile file, Long communityId) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file, "communities", communityId);
    }

    private FileInfo uploadFile(MultipartFile file, String type, Long entityId) throws FileUploadException {
        String newFileName = FileUtil.changeFileName(file);
        createDirectory(type, entityId);
        return createFile(file, type, entityId, newFileName);
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

    private FileInfo createFile(MultipartFile file, String type, Long entityId, String newFileName) {

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
