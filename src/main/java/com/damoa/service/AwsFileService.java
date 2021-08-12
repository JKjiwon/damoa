package com.damoa.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.damoa.util.FileUtil;
import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.dto.post.PostUploadImage;
import com.damoa.web.exception.business.FileUploadException;
import com.damoa.web.exception.business.NotImageFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsFileService implements FileService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.baseuri}")
    private String bucketUri;

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
        if (filePath == null) return;

        String key = filePath.substring(bucketUri.length());
        if (amazonS3Client.doesObjectExist(bucketName, key)) {
            amazonS3Client.deleteObject(bucketName, key);
        }
    }

    private String getDirectoryPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                .replace("/", File.separator);
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private UploadFile createUploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String newFileName = FileUtil.changeFileName(originalFilename);
        String filePath = getDirectoryPath() + File.separator + newFileName;

        try {
            File uploadFile = convert(file)
                    .orElseThrow(() -> new FileUploadException("Could not convert MultipartFile to File."));
            String saveFilePath = putS3(uploadFile, filePath);
            removeNewFile(uploadFile);

            return new UploadFile(originalFilename, saveFilePath);

        } catch (IOException e) {
            throw new FileUploadException("Could not store file. Please try again.");
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    private void checkImageFile(MultipartFile file) {
        boolean isImage = file.getContentType().startsWith("image");

        if (!isImage) {
            throw new NotImageFileException(("Not an image file"));
        }
    }
}
