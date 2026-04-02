package com.example.demo.baove.service;

import com.example.demo.baove.controller.DTO.theloaiDTO;
import com.example.demo.baove.entity.ComicDanhMuc;
import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.mapper.mapperComic_danhmuc;
import com.example.demo.baove.repository.ComicDanhMucRepository;
import com.example.demo.baove.repository.DanhMucRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class GenreService {
    private final int size = 20;
    @Autowired
    DanhMucRepository danhMucRepository;
    @Autowired
    ComicDanhMucRepository comicDanhMucRepository;
    @Autowired
    private mapperComic_danhmuc mapperComicDanhmuc;

    public DanhMuc danhMucSearch(String genre){
        return danhMucRepository.findByTenDanhMuc(genre);
    }

    public ResponseEntity<?> danhMuccomicsSearch(String genre, int pageNO) {
        DanhMuc danhMuc = danhMucRepository.findByTenDanhMuc(genre);
        if (danhMuc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Pageable pageable = PageRequest.of(pageNO - 1, size, Sort.by("id").ascending());

        Page<ComicDanhMuc> pageComic = comicDanhMucRepository.findByDanhMucId(danhMuc.getId(), pageable);

        if (pageComic == null || pageComic.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("content", List.of());
            emptyResponse.put("currentPage", pageNO);
            emptyResponse.put("totalPages", 0);
            return ResponseEntity.ok(emptyResponse);
        }

        List<theloaiDTO> danhSachDTO = pageComic.getContent().stream()
                .map(comicDanhMuc -> {
                    try {
                        return mapperComicDanhmuc.danhmuc_comicgigidoDto(comicDanhMuc);
                    } catch (Exception e) {
                        log.error("Lỗi ánh xạ ComicDanhMuc sang DTO: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        Map<String, Object> response = new HashMap<>();
        response.put("content", danhSachDTO);
        response.put("currentPage", pageNO);
        response.put("totalPages", pageComic.getTotalPages());
        response.put("totalElements", pageComic.getTotalElements());

        return ResponseEntity.ok(response);
    }


    public List<DanhMuc> fillAllGenre (){
        return danhMucRepository.findAll();
    }


}
