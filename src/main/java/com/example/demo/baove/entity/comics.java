package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "COMICS")
public class comics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

    @Column(name = "TEN_TRUYEN")
    private String tenTruyen;

    @Column(name = "LUOT_THICH")
    private int luotThich;

    @Column(name = "LUOT_XEM")
    private int luotXem;

    @Column(name = "MO_TA")
    private String moTa;

    @Column(name = "GHI_CHU")
    private String ghiChu;
}
