package com.example.demo.Controller.nhanvien;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.Tinh_TP;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.nhanVien.TinhRepository;
import com.example.demo.service.EmailService;
import com.example.demo.services.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/panda/nhanvien/tkkhachhang")
public class XemTKKhachHangController {
    @Autowired
    KhachHangService khachHangService;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TinhRepository tinhRepository;
    @GetMapping("/hienthi")
    public String khachhang(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "makh", required = false) String makh,
                            @RequestParam(value = "tenkh", required = false) String tenkh,
                            @RequestParam(value = "trangThai", required = false) Integer trangThai,
                            Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        if (page < 0) {
            page = 0;
        }
        Page<KhachHang> listKH = khachHangService.hienThiKH(page, makh , tenkh  , trangThai);
        model.addAttribute("totalPage", listKH.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("list", listKH.getContent());
        model.addAttribute("makh", makh);
        model.addAttribute("tenkh", tenkh);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("pageSize", listKH.getSize());
        List<KhachHang> listKhachHang = khachHangRepository.findAll(); // Lấy danh sách khách hàng
        model.addAttribute("listKhachHang", listKhachHang); // Thêm vào model
        return "/nhanvien/XemTKKH";
    }
    @GetMapping("/chitiet")
    @ResponseBody
    public KhachHang chiTietKhachHang(@RequestParam("id") Integer id) {
        return khachHangService.findById(id);
    }
}
