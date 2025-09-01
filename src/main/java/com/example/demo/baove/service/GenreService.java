package com.example.demo.baove.service;

import com.example.demo.baove.controller.DTO.theloaiDTO;
import com.example.demo.baove.entity.ComicDanhMuc;
import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.mapper.mapperComic_danhmuc;
import com.example.demo.baove.repository.ComicDanhMucRepository;
import com.example.demo.baove.repository.DanhMucRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class GenreService {
    @Autowired
    DanhMucRepository danhMucRepository;
    @Autowired
    ComicDanhMucRepository comicDanhMucRepository;
    @Autowired
    private mapperComic_danhmuc mapperComicDanhmuc;

    public DanhMuc danhMucSearch(String genre){
        return danhMucRepository.findByTenDanhMuc(genre);
    }

    public ResponseEntity<List<theloaiDTO>> danhMuccomicsSearch(String genre) {
        DanhMuc danhMuc = danhMucRepository.findByTenDanhMuc(genre);
        if (danhMuc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        List<ComicDanhMuc> danhSachComic = comicDanhMucRepository.findByDanhMucId(danhMuc.getId());
        if (danhSachComic == null || danhSachComic.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<theloaiDTO> danhSachDTO = danhSachComic.stream()
                .sorted(Comparator.comparing(ComicDanhMuc::getId))
                .map(comicDanhMuc -> {
                    try {
                        return mapperComicDanhmuc.danhmuc_comicgigidoDto(comicDanhMuc);
                    } catch (Exception e) {
                        log.error("Lỗi ánh xạ ComicDanhMuc sang DTO: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(danhSachDTO);
    }

}
