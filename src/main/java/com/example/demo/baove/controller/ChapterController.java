package com.example.demo.baove.controller;

import com.example.demo.baove.entity.Chapter;
import com.example.demo.baove.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterRepository chapterRepository;

    @GetMapping
    public List<Chapter> getChaptersByComicId(@RequestParam("id_comic") int idComic) {
        return chapterRepository.findByIdComic(idComic);
    }
}