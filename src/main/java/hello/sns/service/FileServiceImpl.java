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

    @Override
    public FileInfo uploadImageFile(MultipartFile file, Long memberId) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file, memberId);
    }

    private FileInfo uploadFile(MultipartFile file, Long memberId) throws FileUploadException {
        String newFileName = FileUtil.changeFileName(file);
        createDirectory(memberId);
        return createFile(file, memberId, newFileName);
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null) {
            new File(filePath).delete();
        }
    }

    private void createDirectory(Long memberId) {

        StringBuilder dirPath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(memberId);

        File directory = new File(String.valueOf(dirPath));

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private FileInfo createFile(MultipartFile file, Long memberId, String newFileName) {

        StringBuilder filePath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(memberId)
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

        boolean isImage = file.getContentType().startsWith("image");
        if (!isImage) {
            throw new FileUploadException(("Not an image file"));
        }
    }
}
