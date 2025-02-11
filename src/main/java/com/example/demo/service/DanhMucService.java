package com.example.demo.service;

import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.KichThuoc;
import com.example.demo.respository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanhMucService {
    @Autowired
    DanhMucRepository danhMucRepository;

    private final int size = 5;
    public Page<DanhMuc> hienThiDM(int page, String tendm, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return danhMucRepository.findByTenAndTrangthaiDM(tendm, trangthai, pageable);
    }
    public List<DanhMuc> danhMuclist (){
        return danhMucRepository.findAll();
    }
}
