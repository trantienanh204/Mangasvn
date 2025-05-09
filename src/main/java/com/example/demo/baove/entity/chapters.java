package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CHAPTERS")
public class chapters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "TEN_CHAP")
    private String tenChap;

    @Column(name = "TRANG_THAI")
    private Boolean trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;

    @Column(name = "NGAY_SUA")
    private LocalDate ngaySua;

}
