package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WISHLIST")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_USER",referencedColumnName = "id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC",referencedColumnName = "id")
    private comics comics;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;
}
