package com.damoa.util;

import com.damoa.web.dto.common.UploadFile;
import com.damoa.web.dto.post.PostUploadImage;

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