package com.example.demo.baove.controller;

import com.example.demo.baove.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@Controller
@RequestMapping("/api/admintest")
public class testController {

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> testAdmin(Principal principal) {
        return ResponseEntity.ok("Chỉ admin truy cập được" + principal.getName());
    }


}