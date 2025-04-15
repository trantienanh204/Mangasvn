package com.example.demo.baove.controller;

import com.example.demo.baove.entity.Users;
import com.example.demo.baove.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


    @Controller
    @RequestMapping("/test")
    public class nhanvienController {

    @Autowired
    userRepository userRepository ;

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Users> getUserById(@PathVariable("id") Integer id) {
        Optional<Users> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



}

