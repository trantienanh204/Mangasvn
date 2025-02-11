package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "CHI_TIET_VAI_TRO")
public class ChiTietVaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer idChiTietVaiTro;

    @ManyToOne
    @JoinColumn(name = "ID_VAI_TRO")
    private VaiTro vaiTro;

    @ManyToOne
    @JoinColumn(name = "TEN_DANG_NHAP")
    private TaiKhoan taiKhoan;

    public ChiTietVaiTro(VaiTro vaiTro, TaiKhoan taiKhoan) {
        this.vaiTro = vaiTro;
        this.taiKhoan = taiKhoan;
    }


}


