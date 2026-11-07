package com.example.demo.baove.controller;

import com.example.demo.baove.controller.error.messageError;
import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.service.GenreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("theloai")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping("/search/{genre}")
    public ResponseEntity<?> searchGenre(@PathVariable String genre) {
        DanhMuc danhMuc = genreService.danhMucSearch(genre);
        if (danhMuc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageError("Thể loại '" + genre + "' không được tìm thấy."));
        }
        return ResponseEntity.ok(danhMuc);
    }

    @GetMapping("/translate/{genre}")
    public ResponseEntity<?> listGenre(
            @PathVariable String genre,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "page", defaultValue = "1") int pageNO) {
        return genreService.danhMuccomicsSearch(genre, sort, pageNO);
    }

    @GetMapping("/fill/AllGenre")
    public List<DanhMuc> fillGenre() {
        return genreService.fillAllGenre();
    }
}
