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
@Table(name = "HOA_DON_VOUCHER")
public class HoaDon_VouCher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "SO_TIEN_CON_LAI")
    private BigDecimal sotienconlai;

    @ManyToOne
    @JoinColumn(name = "ID_HOA_DON_CHI_TIET",referencedColumnName = "id")
    private HoaDonCT hoaDonCT;

    @ManyToOne
    @JoinColumn(name = "ID_VOUCHER",referencedColumnName = "id")
    private Voucher voucher;
}
