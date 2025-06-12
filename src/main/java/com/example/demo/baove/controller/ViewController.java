package com.example.demo.baove.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/read/{id}")
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
    @GetMapping("theloai")
    public String theloai() {
        return "tonghop";
    }
    @GetMapping("/api/admin")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CHUTUT')")
    public String noideadminORchututlamviec(){
        return "admin";
    }
}
