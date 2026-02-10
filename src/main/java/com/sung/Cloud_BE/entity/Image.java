package com.sung.Cloud_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName; // 원본 파일명 (예: photo.jpg)

    @Column(nullable = false, unique = true)
    private String storedFileName; // 서버에 저장된 파일명 (UUID 기반)

    @Column(nullable = false)
    private String filePath; // 저장 경로 (예: /uploads/abc-123.jpg)

    private Long fileSize; // 파일 크기 (bytes)

    @Column(nullable = false)
    private LocalDateTime uploadedAt; // 업로드 시간

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}
