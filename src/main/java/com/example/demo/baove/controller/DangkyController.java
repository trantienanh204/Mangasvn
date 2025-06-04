package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.LoginDTO;
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
            return ResponseEntity.status(401).body(new ErrorResponse("Không được để trống: " +
                    (request.getUserName() != null ? request.getUserName() : "") + " " +
                    (request.getPassword() != null ? request.getPassword() : "") + " " +
                    (request.getEmail() != null ? request.getEmail() : "")));
        }
        try {
            userService.registerUser(request.getUserName(), request.getPassword(), request.getEmail());
            return ResponseEntity.ok(new SuccessResponse("User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }
    }


    public class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}

