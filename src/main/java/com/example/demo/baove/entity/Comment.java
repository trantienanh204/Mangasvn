package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "COMMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_USER", referencedColumnName = "id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC", referencedColumnName = "id")
    private Comic comics;

    @ManyToOne
    @JoinColumn(name = "ID_CHAPTER", referencedColumnName = "id")
    private Chapter chapters;

    @Column(name = "comment")
    private String comment;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;
}