package com.example.demo.baove.controller.DTO;

import com.example.demo.baove.entity.User;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class theloaiDTO {
    private Integer id;
    private String tenTruyen;
    private String moTa;
    private String imageComic;
    private Integer luotXem;
    private Integer luotThich;
    private String tenDanhMuc;

}
