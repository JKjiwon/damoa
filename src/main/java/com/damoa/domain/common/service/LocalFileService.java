package com.damoa.domain.common.service;

import com.damoa.domain.common.util.FileUtil;
import com.damoa.domain.common.dto.UploadFile;
import com.damoa.domain.post.dto.PostUploadImage;
import com.damoa.domain.common.exception.FileUploadException;
import com.damoa.domain.common.exception.NotImageFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class LocalFileService implements FileService {

    @Value("${damoa.file.image.upload.path}")
    private String fileDir;

    private String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    @Override
    public UploadFile storeImage(MultipartFile file) throws FileUploadException {
        checkImageFile(file);
        return storeFile(file);
    }

    @Override
    public List<PostUploadImage> storePostImages(List<MultipartFile> files) throws FileUploadException {
        files.forEach(this::checkImageFile);
        List<UploadFile> uploadFiles = storeFiles(files);

        return uploadFiles.stream()
                .map(uploadFile -> FileUtil.toPostUploadImage(uploadFile, uploadFiles.indexOf(uploadFile) + 1))
                .collect(Collectors.toList());
    }

    private List<UploadFile> storeFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::storeFile)
                .collect(Collectors.toList());
    }

    private UploadFile storeFile(MultipartFile file) throws FileUploadException {
        return createUploadFile(file);
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null) {
            new File(filePath).delete();
        }
    }

    private String getDirectoryPath() {
        String dirPath = File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                .replace("/", File.separator);

        String dirFullPath = getFullPath(dirPath);
        File directory = new File(dirFullPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return dirPath;
    }

    private UploadFile createUploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String newFileName = FileUtil.changeFileName(originalFilename);

        String filePath = getDirectoryPath() + File.separator + newFileName;
        String storeFileName = getFullPath(filePath);

        try {
            file.transferTo(new File(storeFileName));
            return new UploadFile(originalFilename, filePath);
        } catch (IOException e) {
            throw new FileUploadException("Could not store file. Please try again.");
        }
    }

    private void checkImageFile(MultipartFile file) {
        boolean isImage = file.getContentType().startsWith("image");

        if (!isImage) {
            throw new NotImageFileException(("Not an image file. Please change file."));
        }
    }
}