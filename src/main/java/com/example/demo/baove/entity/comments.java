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
@Table(name = "COMMENTS")
public class comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_USER",referencedColumnName = "id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "ID_COMIC",referencedColumnName = "id")
    private comics comics;

    @ManyToOne
    @JoinColumn(name = "ID_CHAPTER",referencedColumnName = "id")
    private chapters chapters;

    @Column(name = "comment")
    private String comment;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;
}
