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
@Table(name = "THANH_TOAN")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "HINH_THUC_THANH_TOAN")
    private String hinhthucthanhtoan;

    @ManyToOne
    @JoinColumn(name = "ID_KHACH_HANG",referencedColumnName = "id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "ID_HOA_DON",referencedColumnName = "id")
    private HoaDon hoaDon;
}
