package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "COMICS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comic {
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

 @Column(name = "IMAGE_COMIC")
 private String imageComic;

 @Column(name = "NGAY_TAO")
 private LocalDate ngayTao;

 @Column(name = "NGAY_SUA")
 private LocalDate ngaySua;

 @ManyToOne
 @JoinColumn(name = "id_translator")
 private User translator;
}