package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANG_THAI_DON_HANG")
public class TrangThaiDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "TEN_TRANG_THAI")
    private String tentrangthai;

    @Column(name = "ANH_TRANG_THAI_DON_HANG")
    private byte[] anhtrangthai;
}
