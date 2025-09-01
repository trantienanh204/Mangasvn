package com.example.demo.baove.controller;


import com.example.demo.baove.controller.error.messageError;
import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.service.GenreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("theloai")
public class GenreController {
    @Autowired
    GenreService genreService;



    @GetMapping("/search/{genre}")
    public ResponseEntity<?> searchGenre(@PathVariable String genre) {
        DanhMuc danhMuc = genreService.danhMucSearch(genre);
        if (danhMuc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageError("Thể loại '" + genre + "' không được tìm thấy."));
        }
        return ResponseEntity.ok(danhMuc);
    }
    @GetMapping("{genre}")
    public ResponseEntity<?> listGenre(@PathVariable String genre){
       return genreService.danhMuccomicsSearch(genre);
    }

}
