package com.example.demo.Controller.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class demohome {
    @Autowired
    HttpServletRequest request; // dung if else để phân quyền (bảo vệ từng đoạn code - khác với bảo vệ )url

    @GetMapping("/home/index")
    public String index(Model model){
//        if(!request.isUserInRole("ADMIN") || !request.isUserInRole("USER")){ // nếu người đăng nhạp không phải admin
//                return "trở về trang báo lỗi hoặc đăng nhập";
//        }
//        if(request.getRemoteUser() == null){ // nếu người dùng ch đăng nhập
//
//        }
        model.addAttribute("message","Đây là index");
        return "home";
    }

    //@PreAuthorize("hasAnyRole('ADMIN','USER')") //Dùng bảo vệ từng đoạn mã
    //@PreAuthorize("hasRole('ADMIN')")
    //@PreAuthorize("isAuthenticated()") //phải đăng nhập mới có thể sư dụng
    @GetMapping("/home/about")
    public String about(Model model){
        model.addAttribute("message","Đây là about");
        return "home";
    }
}
