package com.oopsw.matna.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    String save(MultipartFile file, String directory) throws IOException;
    void delete(String imageUrl) throws IOException;
}
