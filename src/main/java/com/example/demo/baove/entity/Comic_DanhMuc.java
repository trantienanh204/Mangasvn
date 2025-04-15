package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMIC_DANHMUC")
public class Comic_DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_DANH_MUC",referencedColumnName = "id")
    private TacGia tacGia;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC",referencedColumnName = "id")
    private comics comics;
}
