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
@Table(name = "SAN_PHAM_LOI")
public class SanPhamLoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    @Column(name = "TEN_SAN_PHAM_LOI")
    private String tensploi;

    @Column(name = "MO_TA")
    private String mota;

    @Column(name = "SO_LUONG")
    private Integer soluong;

    @ManyToOne
    @JoinColumn(name = "ID_HOA_DON_CHI_TIET",referencedColumnName = "id")
    private HoaDonCT hoaDonCT;
}
