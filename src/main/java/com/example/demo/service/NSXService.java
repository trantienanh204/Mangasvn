package com.example.demo.service;

import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.NhaSanXuat;
import com.example.demo.respository.NSXRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NSXService {
    @Autowired
    NSXRepository nsxRepository;

    private final int size = 5;
    public Page<NhaSanXuat> hienThiNSX(int page, String tennsx, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return nsxRepository.findByTenAndTrangthaiNSX(tennsx, trangthai, pageable);
    }
}
