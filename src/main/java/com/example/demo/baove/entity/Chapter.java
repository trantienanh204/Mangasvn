package com.example.demo.baove.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "CHAPTERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
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

    @Column(name = "ID_COMIC")
    private int idComic;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC", insertable = false, updatable = false)
    @JsonBackReference
    private Comic comic;
}