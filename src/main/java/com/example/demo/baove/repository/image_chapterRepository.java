package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Image_Chapters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface image_chapterRepository extends JpaRepository<Image_Chapters,Integer> {
}
