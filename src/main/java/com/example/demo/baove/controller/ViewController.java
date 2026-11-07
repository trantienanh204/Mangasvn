package com.example.demo.baove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/truyen/{id}")
    public String readPage(@PathVariable("id") int id) {
        return "read";
    }

    @GetMapping("/view/history")
    public String history() {
        return "history";
    }

    @GetMapping("/view/favourite")
    public String favourite() {
        return "wishlist";
    }

    @GetMapping({"/theloai", "/theloai/{genre}"})
    public String theloai() {
        return "theloai";
    }

    @GetMapping("/view/test")
    public String test() {
        return "test";
    }

    @GetMapping("/view/admin")
    public String noideadminORchututlamviec(){
        return "admin";
    }

    @GetMapping({"/login", "/dangnhap", "/login_dangnhap.html"})
    public String login() {
        return "login_dangnhap";
    }

    @GetMapping({"/register", "/dangky", "/dangky.html"})
    public String register() {
        return "dangky";
    }

    @GetMapping("/")
    public String home() {
        return "forward:/view/trangchu.html";
    }
}
