package com.example.demo.baove.service;

import com.example.demo.baove.controller.DTO.ComicDTO;
import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class comicService {
    @Autowired
    private comicRepository comicRepository;

    @Autowired
    private UserRepository userRepository;

    private final Integer size = 12;

    public List<Comic> searchALL(List<Integer> id, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Comic> result = comicRepository.findByDanhMucIds(id, pageable);
        return result.getContent();
    }

    public List<Comic> searchcomictag(Integer id, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Comic> result = comicRepository.findByDanhMucId(id, pageable);
        return result.getContent();
    }
    @Transactional(readOnly = true)
    public Page<ComicDTO> fillListAll(int page) {
        Pageable pageable = PageRequest.of(page, 2, Sort.by("id").descending());
        Page<Comic> comicPage = comicRepository.findAllWithCollections(pageable);
        return comicPage.map(this::convertToDTO);
    }

    private ComicDTO convertToDTO(Comic comic) {
        ComicDTO dto = new ComicDTO();
        dto.setId(comic.getId());
        dto.setTenTruyen(comic.getTenTruyen());
        dto.setMoTa(comic.getMoTa());
        dto.setGhiChu(comic.getGhiChu());
        dto.setImageComic(comic.getImageComic());
        dto.setLuotXem(comic.getLuotXem());
        dto.setLuotThich(comic.getLuotThich());
        dto.setNgayTao(comic.getNgayTao());
        dto.setNgaySua(comic.getNgaySua());

        if (comic.getComicDanhMucs() != null) {
            dto.setCategoryIds(comic.getComicDanhMucs().stream()
                    .map(cdm -> cdm.getDanhMuc().getId())
                    .collect(Collectors.toList()));
        }

        if (comic.getComicTacGias() != null) {
            dto.setAuthorIds(comic.getComicTacGias().stream()
                    .map(ctg -> ctg.getTacGia().getId())
                    .collect(Collectors.toList()));
        }
        return dto;
    }


}
