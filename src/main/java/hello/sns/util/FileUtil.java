package hello.sns.util;

import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.post.PostImageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileUtil {

    public static String changeFileName(MultipartFile file) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
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

    public static PostImageInfo toPostImageInfo(int postId, FileInfo fileInfo, int seq) {
        return new PostImageInfo(postId, fileInfo.getFileName(), fileInfo.getFilePath(), seq);
    }
}
