package com.example.demo.Controller.nhanvien;

import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.SanPhamChiTietRepository;
import com.example.demo.service.SanPhamService;
import com.example.demo.services.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Base64;

import java.util.List;

@Controller
@RequestMapping("/panda/nhanvien/xemsanpham")
public class XemSanPhamController {
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @GetMapping("/hienthi")
    public String khachhang(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "tensp", required = false) String tensp,
                            @RequestParam(value = "trangThai", required = false) Integer trangThai,
                            Model model) {

        String role = "nhanvien"; // Hoặc lấy giá trị role từ session hoặc service

        model.addAttribute("role", role);

        if (page < 0) {
            page = 0;
        }

        Page<SanPham> listSP = sanPhamService.hienThiSanPhamTheoDieuKien(page, tensp, trangThai);

        // Chuyển đổi ảnh sang chuỗi Base64
        listSP.getContent().forEach(sp -> {
            if (sp.getAnhsp() != null) {
                String base64Image = Base64.getEncoder().encodeToString(sp.getAnhsp());
                sp.setBase64Image(base64Image); // Cập nhật chuỗi Base64 vào thuộc tính tạm thời
            }
        });


        model.addAttribute("totalPage", listSP.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("listsp", listSP.getContent());
        model.addAttribute("tensp", tensp);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("pageSize", listSP.getSize());


        return "/nhanvien/XemSanPham";
    }



    @GetMapping("/chitiet")
    public String chiTietHoaDon(@RequestParam("id") Integer id, Model model) {
        List<SanPhamChiTiet> spct = sanPhamChiTietRepository.findsanphamct(id);
        model.addAttribute("spct", spct);
        return "/nhanvien/XemSanPham :: chiTiet"; // Trả về fragment HTML
    }
}
