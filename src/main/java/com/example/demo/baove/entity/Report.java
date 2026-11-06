package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "REPORT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_USER", referencedColumnName = "id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC", referencedColumnName = "id")
    private Comic comics;

    @Column(name = "LY_DO", columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "TRANG_THAI")
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;
}
