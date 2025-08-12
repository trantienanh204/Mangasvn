package com.example.demo.baove.service;

import com.example.demo.baove.controller.DTO.theloaiDTO;
import com.example.demo.baove.entity.ComicDanhMuc;
import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.mapper.mapperComic_danhmuc;
import com.example.demo.baove.repository.ComicDanhMucRepository;
import com.example.demo.baove.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<ComicDanhMuc> danhSachComic = comicDanhMucRepository.findByDanhMucId(danhMuc.getId());
        List<theloaiDTO> danhSachDTO = danhSachComic.stream()
                .sorted()
                .map(mapperComicDanhmuc::danhmuc_comicgigidoDto)
                .toList();
        return ResponseEntity.ok(danhSachDTO);
    }

}
