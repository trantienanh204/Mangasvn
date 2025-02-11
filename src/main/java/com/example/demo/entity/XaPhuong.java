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
@Table(name = "XA_PHUONG")
public class XaPhuong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    @Column(name = "TEN_XA_PHUONG")
    private String tenxaphuong;

    @ManyToOne
    @JoinColumn(name = "ID_QUAN_HUYEN",referencedColumnName = "id")
    private QuanHuyen quanHuyen;
}
