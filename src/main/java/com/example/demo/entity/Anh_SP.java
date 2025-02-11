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
@Table(name = "HINH_ANH_SAN_PHAM")
public class Anh_SP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "TEN_ANH")
    private String tenanh;

    @Column(name = "DU_LIEU_ANH")
    private byte[] dulieuanh;

    @Column(name = "MO_TA")
    private String mota ;

        @ManyToOne
    @JoinColumn(name = "ID_SAN_PHAM",referencedColumnName = "id")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "ID_MAU_SAC",referencedColumnName = "id")
    private MauSac mauSac;

}
