
package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "NHAN_VIEN")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class NhanVien implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Vui lòng nhập tên nhân viên")
    @Column(name = "TEN_NHAN_VIEN")
    private String tennhanvien;

    @NotBlank(message = "Vui lòng nhập mã nhân viên ")
    @Column(name = "MA_NHAN_VIEN")
    private String manhanvien;

//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
//            message = "Địa chỉ email không hợp lệ")
//    @NotBlank(message = "Vui lòng nhập email ")
    @Column(name = "TEN_TAI_KHOAN")
    private String tentaikhoan;

    @Column(name = "MAT_KHAU")
    private String matkhau;

    @NotBlank(message = "Bạn chưa chọn chức vụ")
    @Column(name = "CHUC_VU")
    private String chucvu;

    @Column(name = "TRANG_THAI")
    private Integer trangthai;

    @Column(name = "NGAY_TAO")
    private LocalDate ngaytao;

    @Column(name = "NGAY_SUA")
    private LocalDate ngaysua;


    @Column(name = "AVATAR")
    private byte[] image;

    @Column(name = "GIOI_TINH")
    private Integer gioitinh;


    public void toggleTrangThai() {
        this.trangthai = (this.trangthai == 0) ? 1 : 0; // Đảo ngược giá trị giữa 0 và 1
    }

    @Column(name = "DELETEAT")
    private boolean delete;

    @Column(name = "TINH_TRANG")
    private Boolean tinhtrang;

    @OneToOne
    @JoinColumn(name = "TEN_DANG_NHAP") // Tên cột khóa ngoại trong bảng KhachHang

    private TaiKhoan taiKhoan;



}

