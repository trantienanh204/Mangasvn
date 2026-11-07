package com.example.demo.baove.service;

import com.example.demo.baove.controller.DTO.theloaiDTO;
import com.example.demo.baove.entity.Comic;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenreService {
    private final int size = 18;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ComicDanhMucRepository comicDanhMucRepository;

    @Autowired
    private mapperComic_danhmuc mapperComicDanhmuc;

    public DanhMuc danhMucSearch(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            List<DanhMuc> all = danhMucRepository.findAll();
            return all.isEmpty() ? null : all.get(0);
        }

        String decoded = genre;
        try {
            decoded = URLDecoder.decode(genre, StandardCharsets.UTF_8).trim();
        } catch (Exception ignored) {}

        // 1. Tìm theo tên chính xác
        DanhMuc dm = danhMucRepository.findByTenDanhMuc(decoded);
        if (dm != null) return dm;

        // 2. Tìm không phân biệt hoa thường
        dm = danhMucRepository.findByTenDanhMucIgnoreCase(decoded);
        if (dm != null) return dm;

        // 3. Nếu là số ID
        try {
            int id = Integer.parseInt(decoded);
            Optional<DanhMuc> opt = danhMucRepository.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (NumberFormatException ignored) {}

        // 4. Fallback: lấy thể loại đầu tiên nếu không tìm thấy
        List<DanhMuc> list = danhMucRepository.findAll();
        return list.isEmpty() ? null : list.get(0);
    }

    public ResponseEntity<?> danhMuccomicsSearch(String genre, String sortParam, int pageNO) {
        DanhMuc danhMuc = danhMucSearch(genre);
        if (danhMuc == null) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("content", List.of());
            emptyResponse.put("currentPage", pageNO);
            emptyResponse.put("totalPages", 0);
            emptyResponse.put("totalElements", 0);
            return ResponseEntity.ok(emptyResponse);
        }

        // Xử lý sắp xếp linh hoạt
        Sort sort = Sort.by("id").descending();
        if ("views-desc".equalsIgnoreCase(sortParam)) {
            sort = Sort.by("comics.luotXem").descending();
        } else if ("views-asc".equalsIgnoreCase(sortParam)) {
            sort = Sort.by("comics.luotXem").ascending();
        } else if ("date-desc".equalsIgnoreCase(sortParam)) {
            sort = Sort.by("comics.ngayTao").descending();
        } else if ("date-asc".equalsIgnoreCase(sortParam)) {
            sort = Sort.by("comics.ngayTao").ascending();
        } else if ("az".equalsIgnoreCase(sortParam)) {
            sort = Sort.by("comics.tenTruyen").ascending();
        } else if ("za".equalsIgnoreCase(sortParam)) {
            sort = Sort.by("comics.tenTruyen").descending();
        }

        int targetPage = Math.max(0, pageNO - 1);
        Pageable pageable = PageRequest.of(targetPage, size, sort);

        // Chỉ tìm truyện đang hoạt động (TrangThai = 1)
        Page<ComicDanhMuc> pageComic = comicDanhMucRepository.findActiveByDanhMucId(danhMuc.getId(), pageable);

        if (pageComic == null || pageComic.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("content", List.of());
            emptyResponse.put("currentPage", pageNO);
            emptyResponse.put("totalPages", 0);
            emptyResponse.put("totalElements", 0);
            emptyResponse.put("genreInfo", danhMuc);
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
        response.put("genreInfo", danhMuc);

        return ResponseEntity.ok(response);
    }

    public List<DanhMuc> fillAllGenre() {
        return danhMucRepository.findAll();
    }
}
