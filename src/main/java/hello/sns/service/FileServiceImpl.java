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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Value("${sns.upload.path}")
    private String baseDir;

    @Override
    public FileInfo uploadImage(MultipartFile file) throws FileUploadException {
        checkImageFile(file);
        return uploadFile(file);
    }

    @Override
    public List<PostImageInfo> uploadPostImages(List<MultipartFile> files) throws FileUploadException{
        files.forEach(this::checkImageFile);
        List<FileInfo> fileInfos = uploadFiles(files);

        return fileInfos.stream()
                .map(fileInfo -> FileUtil.toPostImageInfo(fileInfo, fileInfos.indexOf(fileInfo) + 1))
                .collect(Collectors.toList());
    }

    private List<FileInfo> uploadFiles(List<MultipartFile> files) {
        Map<String, String> newFileNames = FileUtil.changeFileNames(files);
        createDirectory();

        return files.stream()
                .map(file ->
                        createFileInfo(file, newFileNames.get(file.getOriginalFilename())))
                .collect(Collectors.toList());
    }

    private FileInfo uploadFile(MultipartFile file) throws FileUploadException {
        String newFileName = FileUtil.changeFileName(file);
        createDirectory();
        return createFileInfo(file, newFileName);
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null) {
            new File(filePath).delete();
        }
    }

    private void createDirectory() {
        StringBuilder dirPath = getDirectory();
        File directory = new File(String.valueOf(dirPath));

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private StringBuilder getDirectory() {
        String dirPath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                .replace("/", File.separator);

        return new StringBuilder(baseDir).append(File.separator).append(dirPath);
    }

    private FileInfo createFileInfo(MultipartFile file, String newFileName) {
        StringBuilder filePath = getDirectory().append(File.separator).append(newFileName);
        try {
            file.transferTo(new File(String.valueOf(filePath)));
            return new FileInfo(newFileName, String.valueOf(filePath));
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
