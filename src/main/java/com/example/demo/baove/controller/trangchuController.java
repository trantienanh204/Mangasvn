package com.example.demo.baove.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth/")
public class trangchuController {


    @GetMapping("login")
    public String login(){
        return "login";
    }
    @GetMapping("register")
    public String register(){
        return "dangky";
    }
}
