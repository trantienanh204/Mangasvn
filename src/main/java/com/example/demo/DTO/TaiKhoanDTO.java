package com.example.demo.DTO;


import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TaiKhoanDTO {

    private String tenDangNhap;
    private String matKhau;
    private NhanVienDTO nhanVienDTO;
    private KhachHangDTO khachHangDTO;
    private Set<ChiTietVaiTroDTO> chiTietVaiTros; // Chuyển đổi từ ChiTietVaiTro sang DTO

    //tạm thời cho nhân viên
    public TaiKhoanDTO(String tenDangNhap, String matKhau) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }
    //findByTenDangNhap
    public TaiKhoanDTO(String tenDangNhap, String matKhau, Set<ChiTietVaiTroDTO> chiTietVaiTros) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.chiTietVaiTros = chiTietVaiTros;
    }



}
