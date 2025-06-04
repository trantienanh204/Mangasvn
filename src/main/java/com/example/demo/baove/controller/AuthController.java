package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.LoginDTO;
import com.example.demo.baove.entity.User;
import com.example.demo.baove.security.JwtUtil;
import com.example.demo.baove.service.UserService;
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

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt).replace("ROLE_", "");
            return ResponseEntity.ok(new UserInfoResponse(username, role.toUpperCase()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token không hợp lệ");
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );

            UserDetails userDetails = userService.loadUserByUsername(request.getUserName());
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
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






class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
class UserInfoResponse {
    private String username;
    private String role;

    public UserInfoResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}