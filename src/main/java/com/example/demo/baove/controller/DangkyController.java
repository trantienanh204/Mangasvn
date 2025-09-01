package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.LoginDTO;
import com.example.demo.baove.controller.error.messageError;
import com.example.demo.baove.controller.error.messageSuccess;
import com.example.demo.baove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/auth")
public class DangkyController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginDTO request) {
        if (request.getUserName() == null || request.getPassword() == null || request.getEmail() == null) {
            return ResponseEntity.status(401).body(new messageError("Không được để trống: " +
                    (request.getUserName() != null ? request.getUserName() : "") + " " +
                    (request.getPassword() != null ? request.getPassword() : "") + " " +
                    (request.getEmail() != null ? request.getEmail() : "")));
        }
        try {
            userService.registerUser(request.getUserName(), request.getPassword(), request.getEmail());
            return ResponseEntity.ok(new messageSuccess("User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new messageError(e.getMessage()));
        }
    }





}

