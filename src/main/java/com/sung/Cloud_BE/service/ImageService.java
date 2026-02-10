package com.sung.Cloud_BE.service;

import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sung.Cloud_BE.dto.ImageResponse;
import com.sung.Cloud_BE.entity.Image;
import com.sung.Cloud_BE.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 이미지 업로드 (S3)
     */
    @Transactional
    public ImageResponse uploadImage(MultipartFile file) throws IOException {
        // 1. 고유한 파일명 생성 (UUID + 확장자)
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        String storedFileName = UUID.randomUUID().toString() + extension;

        // 2. S3에 업로드할 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // 3. S3에 파일 업로드
        try {
            amazonS3.putObject(new PutObjectRequest(
                    bucketName,
                    storedFileName,
                    file.getInputStream(),
                    metadata));
        } catch (IOException e) {
            throw new IOException("S3 업로드 실패: " + e.getMessage());
        }

        // 4. S3 URL 생성
        String s3Url = amazonS3.getUrl(bucketName, storedFileName).toString();

        // 5. DB에 메타데이터 저장
        Image image = Image.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(s3Url)
                .fileSize(file.getSize())
                .build();

        Image savedImage = imageRepository.save(image);

        return ImageResponse.from(savedImage);
    }

    /**
     * 모든 이미지 조회 (최신순)
     */
    public List<ImageResponse> getAllImages() {
        return imageRepository.findAllByOrderByUploadedAtDesc()
                .stream()
                .map(ImageResponse::from)
                .collect(Collectors.toList());
    }
}
