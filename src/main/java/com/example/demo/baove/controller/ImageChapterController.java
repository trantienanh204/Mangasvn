package com.example.demo.baove.controller;

import com.example.demo.baove.entity.ImageChapter;
import com.example.demo.baove.repository.ImageChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageChapterController {

    @Autowired
    private ImageChapterRepository imageChapterRepository;

    @GetMapping
    public List<ImageChapter> getImagesByChapterId(@RequestParam("id_chapter") int idChapter) {
        return imageChapterRepository.findByChapterId(idChapter);
    }
}