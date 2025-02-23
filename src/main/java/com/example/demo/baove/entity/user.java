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
@Table(name = "USERS")
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASS")
    private String pass;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TRANG_THAI")
    private boolean trang_Thai;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "NGAY_TAO")
    private LocalDate ngay_Tao;
}
