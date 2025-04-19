package com.example.demo.baove.controller;

import com.example.demo.baove.entity.Users;
import com.example.demo.baove.security.JwtUtil; // Đổi từ jwt_utility thành JwtUtil
import com.example.demo.baove.service.UserService; // Đổi từ userService thành UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Users user = userService.registerUser(
                request.getUserName(),
                request.getPassword(),
                request.getEmail(),
                request.getRole()
        );
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );

            UserDetails userDetails = userService.loadUserByUsername(request.getUserName());
            String role = userDetails.getAuthorities().iterator().next().getAuthority(); // Không loại bỏ ROLE_
            String token = jwtUtil.generateToken(request.getUserName(), role);

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            System.out.println("Đăng nhập thất bại: Sai tên người dùng hoặc mật khẩu");
            return ResponseEntity.status(401).body("Đăng nhập thất bại: Sai tên người dùng hoặc mật khẩu");
        } catch (Exception e) {
            System.out.println("Đăng nhập thất bại: " + e.getMessage());
            return ResponseEntity.status(500).body("Đăng nhập thất bại: " + e.getMessage());
        }
    }
}

class RegisterRequest {
    private String userName;
    private String password;
    private String email;
    private String role;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

class LoginRequest {
    private String userName;
    private String password;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}