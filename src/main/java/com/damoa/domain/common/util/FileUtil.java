package com.damoa.domain.common.util;

import com.damoa.domain.common.dto.UploadFile;
import com.damoa.domain.post.dto.PostUploadImage;

import java.util.UUID;

public class FileUtil {

    public static String changeFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = getExtension(originalFilename);
        return uuid + "." + extension;
    }

    public static PostUploadImage toPostUploadImage(UploadFile uploadFile, int seq) {
        return new PostUploadImage(uploadFile.getFileName(), uploadFile.getFilePath(), seq);
    }

    private static String getExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}