package com.sung.Cloud_BE.dto;

import com.sung.Cloud_BE.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {

    private Long id;
    private String originalFileName;
    private String imageUrl; // 프론트엔드에서 접근할 URL
    private Long fileSize;
    private LocalDateTime uploadedAt;

    // Entity -> DTO 변환
    public static ImageResponse from(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .originalFileName(image.getOriginalFileName())
                .imageUrl("/uploads/" + image.getStoredFileName())
                .fileSize(image.getFileSize())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}
