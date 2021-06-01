package hello.sns.service;

import hello.sns.web.dto.common.FileDto;
import hello.sns.util.FileUtil;
import hello.sns.web.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Value("${sns.upload.path}")
    private String baseDir;

    @Override
    public FileDto uploadFile(MultipartFile file, Long memberId) throws FileUploadException {
        String newFileName = FileUtil.changeFileName(file);
        checkDirectory(memberId);
        return createFileInfo(file, memberId, newFileName);
    }

    @Override
    public List<FileDto> uploadFiles(List<MultipartFile> files,
                                     Long memberId) throws FileUploadException {

        HashMap<String, String> newFileNames = FileUtil.changeFileNames(files);

        checkDirectory(memberId);

        List<FileDto> fileDtos = files.stream()
                .map(file ->
                        createFileInfo(file, memberId, newFileNames.get(file.getOriginalFilename())))
                .collect(Collectors.toList());

        return fileDtos;
    }

    @Override
    public void deleteFile(String filePath) {

        if (filePath != null) {
            new File(filePath).delete();
        }
    }

    private void checkDirectory(Long memberId) {

        StringBuilder dirPath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(memberId);

        File directory = new File(String.valueOf(dirPath));

        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private FileDto createFileInfo(MultipartFile file, Long memberId, String newFileName) throws FileUploadException {

        StringBuilder filePath = new StringBuilder()
                .append(baseDir)
                .append(File.separator)
                .append(memberId)
                .append(File.separator)
                .append(newFileName);

        try {
            file.transferTo(new File(String.valueOf(filePath)));
            FileDto fileDto = new FileDto(newFileName, String.valueOf(filePath));

            return fileDto;
        } catch (IOException e) {
            throw new FileUploadException("파일을 업로드하는데 실패하였습니다.", e);
        }
    }
}
