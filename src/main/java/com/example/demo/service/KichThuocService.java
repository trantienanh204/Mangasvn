package com.example.demo.service;

import com.example.demo.entity.KichThuoc;
import com.example.demo.respository.KichThuocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class KichThuocService {
    @Autowired
    KichThuocRepository kichThuocRepository;

    private final int size = 5;
    public Page<KichThuoc> hienThiKT(int page, String tenkt, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return kichThuocRepository.findByTenAndTrangthaiKT(tenkt, trangthai, pageable);
    }

}
