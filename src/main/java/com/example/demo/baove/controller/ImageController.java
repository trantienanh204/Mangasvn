package com.example.demo.baove.controller;

import com.example.demo.baove.entity.ImageChapter;
import com.example.demo.baove.repository.ImageChapterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ImageChapterRepository imageChapterRepository;

    @GetMapping
    public ResponseEntity<List<ImageDTO>> getImagesByChapterId(@RequestParam("id_chapter") int chapterId) {
        List<ImageChapter> images = entityManager.createQuery(
                "SELECT i FROM ImageChapter i WHERE i.chapter.id = :chapterId ORDER BY i.pageNumber",
                ImageChapter.class)
                .setParameter("chapterId", chapterId)
                .getResultList();

        List<ImageDTO> imageDTOs = images.stream()
                .map(img -> new ImageDTO(img.getImageUrl(), img.getPageNumber()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(imageDTOs);
    }


//    @GetMapping
//    public List<ImageChapter> getImagesByChapterId(@RequestParam("id_chapter") int idChapter) {
//        return imageChapterRepository.findByChapterId(idChapter);
//    }
}

class ImageDTO {
    private String imageUrl;
    private int pageNumber;

    public ImageDTO(String imageUrl, int pageNumber) {
        this.imageUrl = imageUrl;
        this.pageNumber = pageNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}