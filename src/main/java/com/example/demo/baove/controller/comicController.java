package com.example.demo.baove.controller;

import com.example.demo.baove.entity.comics;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.security.JwtUtil;
import com.example.demo.baove.service.comicService;
import com.example.demo.baove.service.UserService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/truyen")
public class comicController {

    @Autowired
    private comicService truyenService;

    @Autowired
    private comicRepository comicRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


//    @GetMapping
//    @PreAuthorize("hasAnyRole('translator', 'admin')")
//    public ResponseEntity<?> getAllTruyen() {
//        return ResponseEntity.ok("Danh sách truyện");
//    }

    @PostMapping
    public ResponseEntity<?> createTruyen(@RequestBody TruyenRequest request) {
        String token = SecurityContextHolder.getContext().getAuthentication().getDetails().toString();
        String username = jwtUtil.getUsernameFromToken(token);
        Integer userId = userService.findByUsername(username).getId();

        comics truyen = truyenService.createTruyen(request.getMoTa(), request.getGhiChu(), userId);
        return ResponseEntity.ok(truyen);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTruyen(@PathVariable Integer id, @RequestBody TruyenRequest request) {
        String token = SecurityContextHolder.getContext().getAuthentication().getDetails().toString();
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Integer userId = userService.findByUsername(username).getId();

        comics truyen = truyenService.updateTruyen(id, request.getMoTa(), request.getGhiChu(), userId, role);
        return ResponseEntity.ok(truyen);
    }

    @GetMapping("/list")
    public ResponseEntity<List<comics>> getAllTruyen() {
        List<comics> truyenList = comicRepository.findAll();
        return ResponseEntity.ok(truyenList);
    }

    @GetMapping("/hot")
    public ResponseEntity<List<comics>> getHotTruyen() {
        // Lấy 3 truyện hot (ví dụ: dựa trên lượt xem, mới nhất, hoặc admin chọn)
        List<comics> hotTruyen = comicRepository.findTop3ByOrderByNgayTaoDesc();
        return ResponseEntity.ok(hotTruyen);
    }
//    @PostMapping("/favorite/{truyenId}")
//    public ResponseEntity<?> addToFavorite(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            String jwt = authorizationHeader.substring(7);
//            String username = jwtUtil.getUsernameFromToken(jwt);
//
//            comics truyen = comicRepository.findById(truyenId)
//                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));
//
//            Favorite favorite = new Favorite();
//            favorite.setUsername(username);
//            favorite.setTruyenId(truyenId);
//            favoriteRepository.save(favorite);
//
//            return ResponseEntity.ok("Thêm vào yêu thích thành công");
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Token không hợp lệ hoặc lỗi: " + e.getMessage());
//        }
//    }
}

//@Entity
//public class Favorite {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String username;
//    private Long truyenId;
//
//    // Getters và setters
//}



class TruyenRequest {
    private String moTa;
    private String ghiChu;

    // Getters and setters
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
