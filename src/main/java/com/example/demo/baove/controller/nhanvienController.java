package com.example.demo.baove.controller;

import ch.qos.logback.core.model.Model;
import com.example.demo.baove.entity.user;
import com.example.demo.baove.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


    @RestController
    @RequestMapping("/test")
    public class nhanvienController {

    @Autowired
    userRepository userRepository ;

    @GetMapping("a")
    public String a(Model model){
    return "view/trangchu";
    }

    @GetMapping("/{id}")
    public ResponseEntity<user> getUserById(@PathVariable("id") Integer id) {
        Optional<user> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



}

