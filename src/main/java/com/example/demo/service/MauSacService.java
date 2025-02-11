package com.example.demo.service;

import com.example.demo.entity.MauSac;
import com.example.demo.entity.SanPham;
import com.example.demo.respository.MauSacRepsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MauSacService {
    @Autowired
    MauSacRepsitory mauSacRepsitory;

    private final int size = 5;

    public Optional<MauSac> finByMauSac(Integer ma_mau_sac) {
        return mauSacRepsitory.findById(ma_mau_sac);
    }

    public List<MauSac> getAllTrangThai() {
        return mauSacRepsitory.findAll();
    }

    public Page<MauSac> hienThimausac(int page, String tenms, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return mauSacRepsitory.findByTenAndTrangthai(tenms, trangthai, pageable);
    }


}
