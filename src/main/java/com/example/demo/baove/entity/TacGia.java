package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "TAC_GIA")
public class TacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "TEN_TAC_GIA")
    private String tenTacGIA;

    @Column(name = "TRANG_THAI")
    private boolean trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;

    @Column(name = "NGAY_SUA")
    private LocalDate ngaySua;


}
