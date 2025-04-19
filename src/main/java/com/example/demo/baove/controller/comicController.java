package com.example.demo.baove.controller;

import com.example.demo.baove.entity.comics;
import com.example.demo.baove.security.JwtUtil;
import com.example.demo.baove.service.comicService;
import com.example.demo.baove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/truyen")
public class comicController {

    @Autowired
    private comicService truyenService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping
    @PreAuthorize("hasAnyRole('translator', 'admin')")
    public ResponseEntity<?> getAllTruyen() {
        return ResponseEntity.ok("Danh sách truyện");
    }

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
}


class TruyenRequest {
    private String moTa;
    private String ghiChu;

    // Getters and setters
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
