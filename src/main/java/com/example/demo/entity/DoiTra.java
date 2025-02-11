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
@Table(name = "DOI_TRA")
public class DoiTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ANH_SAN_PHAM_LOI")
    private byte[] anhsploi;

    @Column(name = "LY_DO_DOI")
    private String lydodoi;

    @Column(name = "NGAY_DOI_TRA")
    private LocalDate ngaydoitra;

    @Column(name = "TRANG_THAI")
    private String trangthai;

    @Column(name = "MO_TA")
    private String mota;

    @ManyToOne
    @JoinColumn(name = "ID_KHACH_HANG",referencedColumnName = "id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "ID_HOA_DON_CHI_TIET",referencedColumnName = "id")
    private HoaDonCT hoaDonCT;
}
