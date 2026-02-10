package com.sung.Cloud_BE.controller;

import com.sung.Cloud_BE.dto.ImageResponse;
import com.sung.Cloud_BE.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 업로드
     */
    @PostMapping
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            ImageResponse response = imageService.uploadImage(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 모든 이미지 조회
     */
    @GetMapping
    public ResponseEntity<List<ImageResponse>> getAllImages() {
        List<ImageResponse> images = imageService.getAllImages();
        return ResponseEntity.ok(images);
    }
}
