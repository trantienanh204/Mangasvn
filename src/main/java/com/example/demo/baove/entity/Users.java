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
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "PASS")
    private String Pass;

    @Column(name = "EMAIL")
    private String Email;

    @Column(name = "TRANG_THAI")
    private boolean trang_Thai;

    @ManyToOne
    @JoinColumn(name = "id_role",referencedColumnName = "id")
    private Role Role;

    @Column(name = "NGAY_TAO")
    private LocalDate ngay_Tao;

    @Column(name = "AVATA")
    private String Avata;
}
