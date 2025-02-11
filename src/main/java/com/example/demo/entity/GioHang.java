package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GIO_HANG")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "SO_LUONG")
    private Integer soluong;

    @Column(name = "TONG_TIEN")
    private BigDecimal tongtien;

    @ManyToOne
    @JoinColumn(name = "ID_KHACH_HANG", referencedColumnName = "id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "ID_SAN_PHAM_CHI_TIET", referencedColumnName = "id")
    private SanPhamChiTiet sanPhamChiTiet;
}
