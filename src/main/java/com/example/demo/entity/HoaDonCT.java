package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

@Entity
@Table(name = "HOA_DON_CHI_TIET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class HoaDonCT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "MAHDCT")
    private String mahoadonct;

    @Column(name = "SO_LUONG")
    private int soluong;

    @Column(name = "TRANG_THAI")
    private int trangthai;

    @Column(name = "DON_GIA")
    private BigDecimal dongia;

    @Column(name = "NGAY_TAO")
    private LocalDate ngaytao;

    @Column(name = "TONG_TIEN")
    private BigDecimal tongtien;

    @Column(name = "HINH_THUC_THANH_TOAN")
    private String hinhthucthanhtoan;

    @ManyToOne
    @JoinColumn(name = "ID_SAN_PHAM_CHI_TIET", referencedColumnName = "id")
    private SanPhamChiTiet sanPhamChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_HOA_DON", referencedColumnName = "id")
    private HoaDon hoaDon;

    public String getFormattongtien() {
        if (tongtien == null) {
            return "0"; // Trả về nếu giá trị null
        }
        try {
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            return formatter.format(tongtien);
        } catch (NumberFormatException e) {
            return "Không hợp lệ"; //
        }
    }
}

