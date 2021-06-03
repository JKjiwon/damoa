package hello.sns.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FileUtil {

    public static String changeFileName(MultipartFile file) {
        String uuid = UUID.randomUUID().toString().replace("-","");
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        StringBuilder newFileName = new StringBuilder()
                .append(uuid)
                .append(".")
                .append(extension);

        return String.valueOf(newFileName);
    }
}
