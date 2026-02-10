package com.sung.Cloud_BE.repository;

import com.sung.Cloud_BE.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // 업로드 시간 기준 내림차순 정렬 (최신 순)
    List<Image> findAllByOrderByUploadedAtDesc();
}
