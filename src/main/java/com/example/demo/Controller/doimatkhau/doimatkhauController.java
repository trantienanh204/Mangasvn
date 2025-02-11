package com.example.demo.Controller.doimatkhau;

import com.example.demo.entity.NhanVien;
import com.example.demo.service.doimatkhauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/panda")

public class doimatkhauController {

    @Autowired
    doimatkhauService doimatkhauService;


    @GetMapping("/doimatkhau")
    public String doimatkhauform(){
        return "/doimatkhau/doimatkhau";
    }

    @PostMapping("/doimatkhau")
    public String doimatkhau(@RequestParam("taikhoan") String taikhoan,
                             @RequestParam("matkhaucu") String matKhauCu,
                             @RequestParam("matkhaumoi") String matKhauMoi,
                             @RequestParam("nhaplaiMatKhauMoi") String nhapLaiMatKhauMoi,
                             Model model){

        String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (taikhoan == null || taikhoan.isEmpty() || !taikhoan.matches(regexEmail)) {
            model.addAttribute("errorMessage", "Tên tài khoản không hợp lệ. Vui lòng nhập địa chỉ email hợp lệ.");
            return "/doimatkhau/doimatkhau";
        }

        NhanVien nhanVien = doimatkhauService.timkiemnhanvien(taikhoan);

        if (nhanVien != null) {
            if (!matKhauCu.equals(nhanVien.getMatkhau())) {
                model.addAttribute("errorMessage", "Mật khẩu cũ không đúng.");
                return "/doimatkhau/doimatkhau";
            }

            if (!matKhauMoi.equals(nhapLaiMatKhauMoi)) {
                model.addAttribute("errorMessage", "Mật khẩu mới không khớp.");
                return "/doimatkhau/doimatkhau";
            }
            nhanVien.setMatkhau(matKhauMoi);

            doimatkhauService.suanhanvien(nhanVien);
            model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "/doimatkhau/doimatkhau";

        } else {
            model.addAttribute("errorMessage", "Không tìm thấy tài khoản.");
            return "/doimatkhau/doimatkhau";

        }
    }
}
