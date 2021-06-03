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
        return uploadFile(file, "member", String.valueOf(memberId));
    }

    @Override
    public FileInfo uploadCommunityImageFile(MultipartFile file, String communityName) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file, "community", communityName);
    }

    private FileInfo uploadFile(MultipartFile file, String type, String uniqueId) throws FileUploadException {
        String newFileName = FileUtil.changeFileName(file);
        createDirectory(type, uniqueId);
        return createFile(file, type, uniqueId, newFileName);
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null) {
            new File(filePath).delete();
        }
    }

    private void createDirectory(String type, String uniqueId) {

        StringBuilder dirPath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(type)
                .append(File.separator)
                .append(uniqueId);

        File directory = new File(String.valueOf(dirPath));

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private FileInfo createFile(MultipartFile file, String type, String uniqueId, String newFileName) {

        StringBuilder filePath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(type)
                .append(File.separator)
                .append(uniqueId)
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
