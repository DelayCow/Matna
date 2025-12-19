package com.oopsw.matna.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class S3ImageStorageService implements ImageStorageService {
    private final S3Client s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String save(MultipartFile file, String directory) throws IOException {
        validateFile(file.getOriginalFilename());
        return uploadImageToS3(file, directory);
    }

    @Override
    public void delete(String imageUrl) throws IOException {

    }

    // [public 메서드] 다중 파일 업로드
    public List<String> upload(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return save(file, ""); // 기본 디렉토리는 빈 문자열
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                    }
                })
                .toList();
    }

    // [private 메서드] 파일 유효성 검증
    private void validateFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 비어있습니다.");
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

        if (!allowedExtensionList.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + extension);
        }
    }

    // [private 메서드] S3에 업로드 (디렉토리 지원)
    private String uploadImageToS3(MultipartFile file, String directory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = Objects.requireNonNull(originalFilename)
                .substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();

        // 파일명 생성
        String fileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;
        String s3Key = directory.isEmpty() ? fileName : directory + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType("image/" + extension)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            log.info("Successfully uploaded file to S3: {}", s3Key);

        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}", originalFilename, e);
            throw new IOException("S3 업로드 실패: " + e.getMessage(), e);
        }

        return s3Client.utilities()
                .getUrl(url -> url.bucket(bucketName).key(s3Key))
                .toString();
    }

    // [private 메서드] URL에서 S3 키 추출
    private String extractKeyFromUrl(String imageUrl) {
        try {
            if (imageUrl.contains(bucketName + ".s3")) {
                // https://bucket-name.s3.region.amazonaws.com/key
                return imageUrl.substring(imageUrl.indexOf(".com/") + 5);
            } else if (imageUrl.contains("s3") && imageUrl.contains(bucketName)) {
                // https://s3.region.amazonaws.com/bucket-name/key
                return imageUrl.substring(imageUrl.indexOf(bucketName + "/") + bucketName.length() + 1);
            } else {
                // 마지막 경로 부분만 추출
                String[] parts = imageUrl.split("/");
                return parts[parts.length - 1];
            }
        } catch (Exception e) {
            log.error("Failed to extract key from URL: {}", imageUrl, e);
            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + imageUrl);
        }
    }
}
