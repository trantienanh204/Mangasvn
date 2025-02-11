package com.example.demo.Controller.nhanvien;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/panda/nhanvien/banhang")
public class BanHangController {

    @GetMapping("/hienthi")
    public String hienthi(Model model){
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "/nhanvien/BanHang";
    }
}
