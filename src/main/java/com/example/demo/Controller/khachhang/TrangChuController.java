package com.example.demo.Controller.khachhang;


import com.example.demo.DTO.KhachHangDTO;
import com.example.demo.DTO.TaiKhoanDTO;
import com.example.demo.entity.*;
import com.example.demo.respository.HoaDonCTRepository;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.nhanVien.DonHangRepository;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.DTO.TaiKhoanDTO;
import com.example.demo.entity.*;
import com.example.demo.service.GioHangService;
import com.example.demo.service.SanPhamService;
import com.example.demo.service.TaiKhoanService;
import com.example.demo.service.TrangchuService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/panda/")
public class TrangChuController {
    @Autowired

    private TrangchuService trangchuService;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private HoaDonCTRepository hoaDonCTRepository;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    DonHangService donHangService;
    @Autowired
    HDCTService hdctService;
    @Autowired
    private KhachHangRepository khachHangRepository;




    @GetMapping("/trangchu")
    public String hienthi(Model model){
        List<SanPham> sanphamList = trangchuService.spfinall();
        List<Map<String, Object>> processedSanPhamList = new ArrayList<>();

        for (SanPham sp : sanphamList) {
            if (sp.getTrangthai() == 1) {
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
        }
        model.addAttribute("sanpham", processedSanPhamList);
        return "/khachhang/TrangChu";
    }


    @GetMapping("/sanphamchitiet/{id}")
    public String sanphamchitiet(Model model, @PathVariable("id") Integer id) {
        Optional<SanPham> sanPham = sanPhamService.findSanPhamById(id);
        if (sanPham.isPresent()) {
            SanPham sp = sanPham.get();

            // Chuyển đổi dữ liệu byte array thành chuỗi base64 nếu không null
            if (sp.getAnhsp() != null) {
                String base64Image = Base64.getEncoder().encodeToString(sp.getAnhsp());
                model.addAttribute("anhspBase64", base64Image); // Truyền dữ liệu base64 vào model
            } else {
                model.addAttribute("anhspBase64", null);
            }

            // Log số lượng sản phẩm chi tiết
            for (SanPhamChiTiet chiTiet : sp.getSanPhamChiTietList()) {
                System.out.println("Sản phẩm chi tiết ID: " + chiTiet.getId() + ", Số lượng: " + chiTiet.getSoluongsanpham());
            }

            model.addAttribute("sanPham", sp);

            // Lấy giá trị min và max
            Double[] minMaxPrice = sanPhamService.getMinMaxPrice(id);
            model.addAttribute("minPrice", minMaxPrice[0]);
            model.addAttribute("maxPrice", minMaxPrice[1]);

            // Lấy danh sách màu sắc
            List<MauSac> colors = sanPhamService.getColors(id);
            model.addAttribute("colors", colors);

            // Lấy danh sách kích thước
            List<Map<String, Object>> sizesAndColors = sanPhamService.getSizesAndColors(id);
            model.addAttribute("sizesAndColors", sizesAndColors);
        }
        return "/khachhang/SPCT";
    }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/giohang")
        public String giohang(Model model, @AuthenticationPrincipal UserDetails userDetails) {
            String tenDangNhap = userDetails.getUsername();
            TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(tenDangNhap);
            if (taiKhoanDto == null || taiKhoanDto.getKhachHangDTO() == null) {
                return "redirect:/panda/login";
            }
            int khachHangId = taiKhoanDto.getKhachHangDTO().getId();
            List<GioHang> cartItems = gioHangService.getCartItems(khachHangId);

            // Chuyển đổi dữ liệu byte array thành chuỗi base64
            List<Map<String, Object>> processedCartItems = cartItems.stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("sanPhamChiTiet", item.getSanPhamChiTiet());
                itemMap.put("soluong", item.getSoluong());

                if (item.getSanPhamChiTiet().getSanPham().getAnhsp() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(item.getSanPhamChiTiet().getSanPham().getAnhsp());
                    itemMap.put("anhspBase64", base64Image);
                } else {
                    itemMap.put("anhspBase64", null);
                }

                itemMap.put("soLuongTon", item.getSanPhamChiTiet().getSoluongsanpham()); // Truyền giá trị soLuongTon
                System.out.println("SanPhamChiTiet ID: " + item.getSanPhamChiTiet().getId() + " - SoLuongTon: " + item.getSanPhamChiTiet().getSoluongsanpham());

                return itemMap;
            }).collect(Collectors.toList());

            model.addAttribute("cartItems", processedCartItems);
            return "/khachhang/GioHang";
        }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/taikhoan")
    public String taikhoan(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String tenDangNhap = userDetails.getUsername();
        TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(tenDangNhap);
        if (taiKhoanDto == null || taiKhoanDto.getKhachHangDTO() == null) {
            return "redirect:/login";
        }
        KhachHang khachHang = khachHangRepository.findById(taiKhoanDto.getKhachHangDTO().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        List<DonHang> donHangs = donHangService.findByKhachHangId(khachHang.getId());

        // Sắp xếp danh sách đơn hàng theo ngày tạo giảm dần
        donHangs.sort(Comparator.comparing(DonHang::getId).reversed());

        List<DonHang> choduyet = donHangs.stream().filter(d -> "Chờ duyệt".equals(d.getTrangThai())).collect(Collectors.toList());
        List<DonHang> daduyet = donHangs.stream().filter(d -> "Đã duyệt".equals(d.getTrangThai())).collect(Collectors.toList());
        List<DonHang> danggiao = donHangs.stream().filter(d -> "Đang giao".equals(d.getTrangThai())).collect(Collectors.toList());
        List<DonHang> hoanthanh = donHangs.stream().filter(d -> "Hoàn thành".equals(d.getTrangThai())).collect(Collectors.toList());
        List<DonHang> dahuy = donHangs.stream().filter(d -> "Đã hủy".equals(d.getTrangThai())).collect(Collectors.toList());

        model.addAttribute("khachHang", khachHang);
        model.addAttribute("choduyet", choduyet);
        model.addAttribute("daduyet", daduyet);
        model.addAttribute("danggiao", danggiao);
        model.addAttribute("hoanthanh", hoanthanh);
        model.addAttribute("dahuy", dahuy);
        return "/khachhang/TaiKhoan";
    }

    @GetMapping("/thanhtoan")
    public String thanhtoan(){
        return "/khachhang/ThanhToan";

    }
    @GetMapping("/test")
    @ResponseBody
    public List<SanPhamChiTiet>  sanphamchitiet(@RequestParam Integer id) {
        System.out.println("ID: " + id);
        List<SanPhamChiTiet> result = trangchuService.timkiemspct(id);
        System.out.println("Result: " + result);
        return result;
    }
    @GetMapping("/donhangchitiet/chitiet")
    public String chiTietHoaDon(@RequestParam("id") Integer id, Model model) {
        try {
            DonHang donHang = donHangRepository.findById(id).orElse(null);
            if (donHang == null) {
                throw new RuntimeException("Đơn hàng không tồn tại.");
            }
            model.addAttribute("DonHang", donHang);

            List<HoaDonCT> hoaDonCT = hdctService.findID(donHang.getHoaDon().getId());
            model.addAttribute("listhdct", hoaDonCT);

            List<SanPhamChiTiet> sanPhamChiTiet = hoaDonCT.stream()
                    .map(hdct -> sanPhamService.Listtimkiemspct(hdct.getSanPhamChiTiet().getId()))
                    .collect(Collectors.toList());
            model.addAttribute("listsp", sanPhamChiTiet);

            return "khachhang/TaiKhoan :: chiTiet"; // Trả về fragment HTML
        } catch (Exception e) {
            // Ghi log lỗi
            System.err.println("Lỗi khi xử lý yêu cầu chi tiết đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return "error"; // Trả về trang lỗi nếu có lỗi xảy ra
        }
    }


}
