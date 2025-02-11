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
@Table(name = "LICH_SU_DON_HANG")
public class LS_DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;


    @Column(name = "NGAY_TAO")
    private LocalDate ngaytao;

    @Column(name = "NGAY_SUA")
    private LocalDate ngaysua;

    @Column(name = "GHI_CHU")
    private String ghichu;

    @ManyToOne
    @JoinColumn(name = "ID_NHAN_VIEN",referencedColumnName = "id")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "ID_TRANG_THAI",referencedColumnName = "id")
    private TrangThaiDonHang trangThaiDonHang;

}
