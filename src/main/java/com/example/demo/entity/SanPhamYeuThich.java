package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SAN_PHAM_YEU_THICH")
public class SanPhamYeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NGAY_TAO")
    private LocalDate ngaytao;

    @ManyToOne
    @JoinColumn(name = "ID_KHACH_HANG",referencedColumnName = "id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "ID_SAN_PHAM",referencedColumnName = "id")
    private SanPham sanPham;

}
