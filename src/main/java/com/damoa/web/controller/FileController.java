package com.damoa.web.controller;

import com.damoa.domain.common.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

//@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

//    @GetMapping("/api/images/**")
//    public Resource downloadImage(HttpServletRequest request) throws MalformedURLException {
//        String filePath = "/" + extractFilePath(request);
//        UrlResource urlResource = new UrlResource("file:" + fileService.getFullPath(filePath));
//        return urlResource;
//    }

    private String extractFilePath(HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        AntPathMatcher apm = new AntPathMatcher();
        return apm.extractPathWithinPattern(bestMatchPattern, path);
    }
}
