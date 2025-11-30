package com.oopsw.matna.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService{

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Override
    public String save(MultipartFile file, String directory) throws IOException {
        Path directoryPath = Paths.get(uploadPath, directory);
        if(!Files.exists(directoryPath)){
            Files.createDirectories(directoryPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path filePath = directoryPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + uploadPath + "/" + directory + "/" + uniqueFilename;
    }

    @Override
    public void delete(String imageUrl) throws IOException {
        Path filePath = Paths.get(imageUrl.substring(1));
        Files.deleteIfExists(filePath);
    }
}
