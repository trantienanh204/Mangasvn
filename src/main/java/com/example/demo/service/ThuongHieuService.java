package com.example.demo.service;

import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.ThuongHieu;
import com.example.demo.respository.ThuongHieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThuongHieuService {
    @Autowired
    ThuongHieuRepository thuongHieuRepository;

    private final int size = 5;
    public Page<ThuongHieu> hienThiTH(int page, String tenth, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return thuongHieuRepository.findByTenAndTrangthaiTH(tenth, trangthai, pageable);
    }

    public  List<ThuongHieu> findAllThuongHieu (){
        return thuongHieuRepository.findAll();
    }
}
