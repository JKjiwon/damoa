package com.damoa.service;

import com.damoa.util.FileUtil;
import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.dto.post.PostUploadImage;
import com.damoa.web.exception.business.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Value("${damoa.file.dir}")
    private String fileDir;

    public String getFullPath(String fileName) {
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
                .map(fileInfo -> FileUtil.toPostUploadImage(fileInfo, uploadFiles.indexOf(fileInfo) + 1))
                .collect(Collectors.toList());
    }

    private List<UploadFile> storeFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::storeFile)
                .collect(Collectors.toList());
    }

    private UploadFile storeFile(MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException(("Not exists a file"));
        }
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
            throw new FileUploadException("Fail to create file");
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