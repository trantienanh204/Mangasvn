package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TAI_KHOAN")
public class TaiKhoan {


        @Id
        @Column(name = "TEN_DANG_NHAP")
        @NotBlank(message = "Tên đăng nhập không được để trống")
        private String tenDangNhap;

        @Column(name = "MAT_KHAU")
        @NotBlank(message = "Mật khẩu không được để trống")
        private String matKhau;

        @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL)
        private NhanVien nhanVien; // Đảm bảo thuộc tính này có mặt

        @OneToOne(mappedBy = "taiKhoan",cascade = CascadeType.ALL)
        @JsonBackReference
        private KhachHang khachHang;

        @OneToMany(mappedBy = "taiKhoan",fetch = FetchType.EAGER)
        private Set<ChiTietVaiTro> chiTietVaiTros;

}
