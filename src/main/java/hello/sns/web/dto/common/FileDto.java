package hello.sns.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileDto {

    private final String fileName;

    private final String filePath;
}
