package com.example.demo.baove.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/read/{id}")
    public String readPage(@PathVariable("id") int id) {
        return "read"; // Trả về read.html
    }
}
