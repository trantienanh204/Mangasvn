package com.example.demo.baove.service;

import com.example.demo.baove.entity.Role;
import com.example.demo.baove.entity.Users;
import com.example.demo.baove.repository.roleRepository;
import com.example.demo.baove.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    private userRepository userRepository;

    @Autowired
    private roleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPass(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()))
        );
    }

    public Users registerUser(String userName, String password, String email, String roleName) {
        Users user = new Users();
        user.setUserName(userName);
        user.setPass(passwordEncoder.encode(password)); // Mã hóa mật khẩu
        user.setEmail(email);
        user.setTrang_Thai(true);
        user.setNgay_Tao(LocalDate.now());

        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        user.setRole(role);

        return userRepository.save(user);
    }
    public Users findByUsername(String username) {
        Users user = userRepository.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }
}
