package com.example.demo.baove.repository;

import com.example.demo.baove.entity.ImageChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageChapterRepository extends JpaRepository<ImageChapter, Integer> {
    List<ImageChapter> findByChapterIdOrderByPageNumberAsc(int chapterId);
    List<ImageChapter> findByChapterId(int chapterId);
}