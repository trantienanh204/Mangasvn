package com.example.demo.baove.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicAdminDTO {
    private int id;
    private String tenTruyen;
    private String imageComic;
    private int luotXem;
    private int luotThich;
    private byte trangThai;
    private LocalDate ngayTao;
    private LocalDate ngaySua;
    private String translatorUsername;
    private List<String> authors;
    private List<String> categories;
    private int chapterCount;
    private boolean canEdit; // true nếu user hiện tại có quyền sửa/ẩn truyện này
}
