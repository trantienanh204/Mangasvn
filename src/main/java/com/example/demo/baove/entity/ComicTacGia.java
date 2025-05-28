package com.example.demo.baove.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMIC_TACGIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicTacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comic", nullable = false)
    @JsonBackReference
    private Comic comics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tac_gia", nullable = false)
    private TacGia tacGia;
}