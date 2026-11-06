package com.example.demo.baove.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private int id;
    private Integer userId;
    private String username;
    private Integer comicId;
    private String comicTitle;
    private String lyDo;
    private String trangThai;
    private LocalDate ngayTao;
}
