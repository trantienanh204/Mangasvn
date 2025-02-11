package com.example.demo.Controller.thongtincanhankh;

import com.example.demo.DTO.KhachHangDTO;
import com.example.demo.DTO.TaiKhoanDTO;
import com.example.demo.entity.DiaChiDTO;
import com.example.demo.entity.KhachHang;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("diachi")
public class diachigiaohangController {

    @Autowired
    TaiKhoanService taiKhoanService;
    @Autowired
    KhachHangRepository khachHangRepository;

    @GetMapping("themdiachi")
    public String themdiachi(@AuthenticationPrincipal UserDetails userDetails, Model model){
        String username = userDetails.getUsername();
        TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(username);
        KhachHang khachHang = mapToKhachHang(taiKhoanDto.getKhachHangDTO());
        model.addAttribute("khachhang", khachHang);
        return "khachhang/themdiachimoi";
    }

    @PostMapping("/api/save-address")
    public ResponseEntity<Map<String, String>> saveAddress(@RequestBody DiaChiDTO diaChiDTO, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(username);
        KhachHang khachHang = khachHangRepository.findById(taiKhoanDto.getKhachHangDTO().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Kiểm tra nếu số điện thoại đã tồn tại
        KhachHang existingKhachHang = khachHangRepository.findBySdtAndNotId(diaChiDTO.getPhoneNumber(), khachHang.getId());
        if (existingKhachHang != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Số điện thoại đã tồn tại, vui lòng sử dụng số khác.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        khachHang.setDiachi(diaChiDTO.getFullAddress());
        khachHang.setSdt(diaChiDTO.getPhoneNumber());
        khachHang.setTenkhachhang(diaChiDTO.getRecipientName());
        khachHangRepository.save(khachHang);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Địa chỉ đã được lưu thành công");
        return ResponseEntity.ok(response);
    }


    private KhachHang mapToKhachHang(KhachHangDTO dto) {
        KhachHang khachHang = new KhachHang();
        khachHang.setId(dto.getId());
        khachHang.setTinhtrang(dto.getTinhtrang());
        khachHang.setDelete(dto.getDelete());
        khachHang.setMakhachhang(dto.getMakhachhang());
        khachHang.setTenkhachhang(dto.getTenkhachhang());
        khachHang.setSdt(dto.getSdt());
        khachHang.setDiachi(dto.getDiachi());
        return khachHang;
    }
}
