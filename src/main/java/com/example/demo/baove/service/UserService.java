package com.example.demo.baove.service;

import com.example.demo.baove.entity.Role;
import com.example.demo.baove.entity.User;
import com.example.demo.baove.repository.RoleRepository;
import com.example.demo.baove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()))
        );
    }

    public User registerUser(String userName, String password, String email) {
        // Kiểm tra trùng userName
        if (userRepository.existsByUsername(userName)) {
            throw new IllegalArgumentException("Tên người dùng '" + userName + "' đã tồn tại!");
        }

        // Kiểm tra trùng email
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setTrangThai(true);
        user.setNgayTao(LocalDate.now());

        Role role = roleRepository.findByRoleName("DOCGIA");
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        user.setRole(role);

        System.out.println("Tên mới tạo: " + user.getUsername() + " email: " + user.getEmail());

        return userRepository.save(user);
    }
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    public User findById(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }
}