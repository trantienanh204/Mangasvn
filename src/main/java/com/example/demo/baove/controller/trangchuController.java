package com.example.demo.baove.controller;


import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class trangchuController {
    @GetMapping()
    public String trangChu(Model model){
        return "view/trangchu";
    }

    @GetMapping("login")
    public String login(Model model){
        return "login";
    }
}
