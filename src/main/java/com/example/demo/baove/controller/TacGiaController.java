package com.example.demo.baove.controller;

import com.example.demo.baove.repository.TacGiaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import com.example.demo.baove.entity.TacGia;
import com.example.demo.baove.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@RequestMapping("/api")
@Controller
public class TacGiaController {
    private static final Logger logger = LoggerFactory.getLogger(comicController.class);

    @Autowired
    TacGiaRepository tacGiaRepository;
    @GetMapping("/authors")
    public ResponseEntity<List<TacGia>> getAllAuthors() {
        try {
            List<TacGia> authors = tacGiaRepository.findAll();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách tác giả: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
