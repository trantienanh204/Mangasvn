package com.example.demo.baove.controller;

import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Controller
@RequestMapping("/api")
public class DanhMucController {
    private static final Logger logger = LoggerFactory.getLogger(comicController.class);

    @Autowired
    DanhMucRepository danhMucRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<DanhMuc>> getAllCategories() {
        try {
            List<DanhMuc> categories = danhMucRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.info("Lỗi khi lấy danh sách danh mục: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
