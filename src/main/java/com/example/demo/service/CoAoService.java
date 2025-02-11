package com.example.demo.service;

import com.example.demo.entity.CoAo;
import com.example.demo.entity.KichThuoc;
import com.example.demo.respository.CoAoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CoAoService {
    @Autowired
    CoAoRepository coAoRepository;

    private final int size = 5;
    public Page<CoAo> hienThiCA(int page, String tenca, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return coAoRepository.findByTenAndTrangthaiCA(tenca, trangthai, pageable);
    }
}
