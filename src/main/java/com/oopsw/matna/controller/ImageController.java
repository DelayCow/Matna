package com.oopsw.matna.controller;

import com.oopsw.matna.service.S3ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    private final S3ImageStorageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> s3Upload(@RequestPart(value = "image") List<MultipartFile> images) throws IOException {
        List<String> upload = imageService.upload(images);
        return ResponseEntity.ok(upload);
    }
}
