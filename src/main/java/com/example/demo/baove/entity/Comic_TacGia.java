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
@Table(name = "COMIC_TACGIA")
public class Comic_TacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_TAC_GIA",referencedColumnName = "id")
    private TacGia tacGia;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC",referencedColumnName = "id")
    private comics comics;
}
