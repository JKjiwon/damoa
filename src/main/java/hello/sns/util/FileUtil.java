package hello.sns.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileUtil {

    public static String changeFileName(MultipartFile file) {
        return String.valueOf(createNewFileName(file));
    }

    public static HashMap<String, String> changeFileNames(List<MultipartFile> files) {
        HashMap<String, String> newFileNames = new HashMap<>();

        for (MultipartFile file : files) {
            newFileNames.put(file.getOriginalFilename(), String.valueOf(createNewFileName(file)));
        }
        return newFileNames;
    }

    private static StringBuilder createNewFileName(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        StringBuilder newFileName = new StringBuilder()
                .append(uuid)
                .append(".")
                .append(extension);

        return newFileName;
    }
}
