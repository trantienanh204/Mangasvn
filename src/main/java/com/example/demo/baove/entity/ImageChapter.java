package com.example.demo.baove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "IMAGE_CHAPTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ID_CHAPTER", referencedColumnName = "id")
    private Chapter chapter;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "PAGE_NUMBER")
    private int pageNumber;

    @Column(name = "NGAY_TAO")
    private LocalDate ngayTao;
}