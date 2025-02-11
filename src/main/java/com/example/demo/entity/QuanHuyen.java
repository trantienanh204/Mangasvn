package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
@Table(name = "QUAN_HUYEN")
public class QuanHuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    @Column(name = "TEN_QUAN_HUYEN")
    private String tenquanhuyen;

    @ManyToOne
    @JoinColumn(name = "ID_TINH_THANH_PHO",referencedColumnName = "id")
    private Tinh_TP tinh_tp;
}
