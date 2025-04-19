package com.example.demo.baove.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class testController {

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('admin')") // Dùng hasAuthority thay vì hasRole
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok("Chỉ admin truy cập được");
    }
}