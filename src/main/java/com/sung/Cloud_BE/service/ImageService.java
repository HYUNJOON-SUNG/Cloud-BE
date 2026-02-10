package com.sung.Cloud_BE.service;

import com.sung.Cloud_BE.dto.ImageResponse;
import com.sung.Cloud_BE.entity.Image;
import com.sung.Cloud_BE.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 이미지 업로드
     */
    @Transactional
    public ImageResponse uploadImage(MultipartFile file) throws IOException {
        // 1. 업로드 디렉토리 생성 (없으면)
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. 고유한 파일명 생성 (UUID + 확장자)
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        String storedFileName = UUID.randomUUID().toString() + extension;

        // 3. 파일 저장
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. DB에 메타데이터 저장
        Image image = Image.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(filePath.toString())
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
