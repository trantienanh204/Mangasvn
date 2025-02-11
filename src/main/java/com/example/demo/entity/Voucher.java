
package com.example.demo.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "VOUCHER")
@Entity
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @NotBlank(message = "Mã không được để trống")
//    @Size(min = 4, max = 12,message = "Mã phải có độ dài từ 4 đến 12 ký tự")
    @Column(name = "MA_KHUYEN_MAI")
    private String ma;

    //    @NotBlank(message = "Tên không được để trống")
//    @Size(min = 1, max = 25,message = "Tên phải có độ dài từ 1 đến 25 ký tự")
    @Column(name = "TEN_KHUYEN_MAI")
    private String ten;

    //    @NotBlank(message = "Số lượng không được để trống")
//    @Max(value = 1000, message = "Số lượng phải bé hơn hoặc bằng 1000")
    @Column(name = "SO_LUONG")
    private String soLuong;

    //    @NotNull(message = "Mức giảm không được để trống")

    @Column(name = "MUC_GIAM")
    private String mucGiam;

    //    @NotBlank(message = "Mô tả không được để trống")
//    @Size(max = 100,message = "Mô tả không được dài hơn 100 ký tự")
    @Column(name = "MO_MA")
    private String mota;


    //    @NotBlank(message = "Điều kiện không trống")
    @Column(name = "DIEU_KIEN_KHUYEN_MAI")
    private String dieuKien;

    @Column(name = "LOAI_KHACH_HANG")
    private boolean loaikhachhang;

    //    @NotNull(message = "Ngay bắt đầu không trống")
    @Column(name = "NGAY_BAT_DAU")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaybatdau;

    //    @NotNull(message = "Ngay kết thúc không trống")
    @Column(name = "NGAY_KET_THUC")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayketthuc;

    @Column(name = "NGAY_TAO")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaytao;
    @Column(name = "NGAY_SUA")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaysua;

    @Column(name = "LOAI")
    private boolean loai;

//    @Column(name = "LOAI_KHACH_HANG")
//    private boolean loaikh;


    @Column(name = "TRANG_THAI")
    private int trangThai;
//
//    @Column(name = "TRANG_THAI")
//    private String trangThai;

    public String getFormattedMucGiam() {
        try {
            double mucGiamValue = Double.parseDouble(this.mucGiam);
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(mucGiamValue);
        } catch (NumberFormatException e) {
            return "Không hợp lệ"; //
        }
    }

    public String getFormattedDieukien() {
        try {
            double dieukien = Double.parseDouble(this.dieuKien);
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(dieukien);
        } catch (NumberFormatException e) {
            return "Không hợp lệ"; //
        }
    }

}

