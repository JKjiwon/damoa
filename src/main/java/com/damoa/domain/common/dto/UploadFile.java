package com.damoa.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFile {

    private final String fileName;

    private final String filePath;
}
