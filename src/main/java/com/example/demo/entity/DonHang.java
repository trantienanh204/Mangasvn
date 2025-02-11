package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DON_HANG")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_HOA_DON", referencedColumnName = "id")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "ID_KHACH_HANG", referencedColumnName = "id")
    private KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "ID_nhan_vien", referencedColumnName = "id")
    private NhanVien nhanVien;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NGAY_TAO")
    private LocalDate ngaytao;


    @Column(name = "TONGTIEN")
    private BigDecimal tongtien;

    @Column(name = "HINH_THUC_MUA")
    private Integer hinhthuc;

    @Column(name = "DIACHI")
    private String diaChi;

    @Column(name = "So_Dien_Thoai")
    private String sdt;

    @Column(name = "Trang_Thai")
    private String trangThai;

    @Column(name = "GHICHU")
    private String ghiChu;

    @Column(name = "Mota")
    private String lydohuy;

    @Column(name = "TRANG_THAI_OFFLINE")
    private Boolean trangthaioffline;

}
