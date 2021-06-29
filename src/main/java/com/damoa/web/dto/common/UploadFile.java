package com.damoa.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFile {

    private final String fileName;

    private final String filePath;
}
