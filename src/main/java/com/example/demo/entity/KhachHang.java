
package com.example.demo.entity;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "KHACH_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
//    @Size(min = 4 , max = 8, message = "Mã từ 4 - 8 ký tự")
//    @NotBlank(message = "Mã khách hàng không được để trố

    @Column(name = "MA_KHACH_HANG")
    private String makhachhang;
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Địa chỉ email không hợp lệ")
//    @NotBlank(message = "Email không được bỏ trống ")
    @Column(name = "TEN_TAI_KHOAN")
    private String tentaikhoan;


    @Column(name = "MAT_KHAU")
    private String matkhau;


    @NotBlank(message = "Số điện thoại không được để trống")
  //  @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và gồm 10 số")
    @Column(name = "SO_DIEN_THOAI")
    private String sdt;

    @Column(name = "DIA_CHI_CU_THE")
    private String diachi;

    @Column(name = "ID_TINH_THANH_PHO")
    private String tinhtp;

    @Column(name = "ID_QUAN_HUYEN")
    private String quanhuyen;

    @Column(name = "ID_XA_PHUONG")
    private String xaphuong;

    @Column(name = "TEN_TINH")
    private String tentinh;

    @Column(name = "TEN_HUYEN")
    private String tenhuyen;

    @Column(name = "TEN_XA")
    private String tenxa;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Column(name = "TEN_KHACH_HANG")
    private String tenkhachhang;
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
        this.trangthai = (this.trangthai == 0) ? 1 : 0;
    }
    public void toggledeleteat() {
        this.delete = !this.delete;
    }
    public void toggletinhtrang() {
        this.tinhtrang = (this.tinhtrang == null || !this.tinhtrang);
    }

//    @ManyToOne
//    @JoinColumn(name = "ID_TINH_THANH_PHO")
//    private Tinh_TP tinh_tp;
//
//    @ManyToOne
//    @JoinColumn(name = "ID_QUAN_HUYEN")
//    private QuanHuyen quanHuyen;
//    @ManyToOne
//    @JoinColumn(name = "ID_XA_PHUONG")
//    private XaPhuong xaPhuong;


    @Column(name = "DELETEAT")
    private boolean delete;

    @Column(name = "TINH_TRANG")
    private Boolean tinhtrang;
    @OneToOne
    @JoinColumn(name = "TEN_DANG_NHAP")
    private TaiKhoan taiKhoan;
}

