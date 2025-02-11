package com.example.demo.Controller.admin.QLSP;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/panda/sanpham")
public class SanPhamController {

    @GetMapping("/hienthi")
    public String hienthi(){
        return "/View/SanPham";
    }

}
