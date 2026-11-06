package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.ReportCreateDTO;
import com.example.demo.baove.controller.DTO.ReportDTO;
import com.example.demo.baove.controller.error.messageError;
import com.example.demo.baove.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private AdminService adminService;

    /**
     * Người dùng gửi báo cáo lỗi về truyện (ảnh lỗi, chương sai, dịch lỗi,...)
     */
    @PostMapping
    public ResponseEntity<?> submitReport(@RequestBody ReportCreateDTO request, Principal principal) {
        if (request.getLyDo() == null || request.getLyDo().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new messageError("Vui lòng nhập lý do báo cáo"));
        }

        String username = (principal != null) ? principal.getName() : null;
        try {
            ReportDTO created = adminService.createReport(request.getComicId(), request.getLyDo(), username);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new messageError("Không thể gửi báo cáo: " + e.getMessage()));
        }
    }
}
