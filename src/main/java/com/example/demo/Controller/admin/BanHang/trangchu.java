package com.example.demo.Controller.admin.BanHang;

import com.example.demo.entity.SanPham;
import com.example.demo.service.GioHangService;
import com.example.demo.service.SanPhamService;
import com.example.demo.service.TaiKhoanService;
import com.example.demo.service.TrangchuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/panda/trangchu")
public class trangchu {
    @Autowired
    TrangchuService trangchuService;
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @GetMapping("show")
    public String show(Model model){
        List<SanPham> sanphamList = trangchuService.spfinall();
        List<Map<String, Object>> processedSanPhamList = new ArrayList<>();

        for (SanPham sp : sanphamList) {
            Map<String, Object> spMap = new HashMap<>();
            spMap.put("id", sp.getId());
            spMap.put("masp", sp.getMasp());
            spMap.put("tensp", sp.getTensp());
            spMap.put("minPrice", sp.getMinPrice());

            if (sp.getAnhsp() != null) {
                // Chuyển đổi dữ liệu byte array thành chuỗi base64
                String base64Image = Base64.getEncoder().encodeToString(sp.getAnhsp());
                spMap.put("anhsp", base64Image);
            } else {
                spMap.put("anhsp", null);
            }

            processedSanPhamList.add(spMap);
        }

        model.addAttribute("sanpham", processedSanPhamList);
        return "trangchu";
    }
}
