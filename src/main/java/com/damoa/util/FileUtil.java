package com.damoa.util;

import com.damoa.web.dto.common.FileInfo;
import com.damoa.web.dto.post.PostImageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileUtil {

    public static String changeFileName(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        StringBuilder newFileName = new StringBuilder()
                .append(uuid)
                .append(".")
                .append(extension);

        return String.valueOf(newFileName);
    }

    public static HashMap<String, String> changeFileNames(List<MultipartFile> files) {

        HashMap<String, String> newFileNames = new HashMap<>();

        for (MultipartFile file : files) {
            newFileNames.put(file.getOriginalFilename(), String.valueOf(changeFileName(file)));
        }
        return newFileNames;
    }

    public static PostImageInfo toPostImageInfo(FileInfo fileInfo, int seq) {
        return new PostImageInfo(fileInfo.getFileName(), fileInfo.getFilePath(), seq);
    }
}
