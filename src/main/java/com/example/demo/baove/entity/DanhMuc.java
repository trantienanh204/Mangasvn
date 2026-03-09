package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "DANH_MUC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "TEN_DANH_MUC")
    private String tenDanhMuc;

    @Column(name = "TRANG_THAI")
    private boolean trangThai;

    @Column(name = "NOI_DUNG")
    private String noiDung;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;

    @Column(name = "NGAY_SUA")
    private LocalDate ngaySua;
}