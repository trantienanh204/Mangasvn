package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/panda")
public class VaiTroController {
    @GetMapping("/vaitro")
    public String vaitro(){
        return "VaiTro";
    }
    @PostMapping("/chia_role")
    public String switchRole(@RequestParam("role") String role, Model model) {
        model.addAttribute("role", role);
        // Điều hướng tới các trang tương ứng với từng role
        if ("admin".equals(role)) {
            return "redirect:/admin/SanPham"; // Admin: chuyển đến trang sản phẩm
        }  else if ("nhanvien".equals(role)) {
            return "redirect:/nhanvien/BanHang"; // PR Staff: chuyển đến trang quản lý bài viết
        } else {
            return "redirect:/dashboard"; // Trường hợp không xác định, quay về dashboard
        }
    }
}
