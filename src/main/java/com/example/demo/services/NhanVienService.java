package com.example.demo.services;

import com.example.demo.entity.NhanVien;
import com.example.demo.respository.NhanVienRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class NhanVienService {
    // phân trang
    @Autowired
    NhanVienRespository nhanVienRespository;
    private final int size = 5;

    public Page<NhanVien> hienThiNV(int page, String manv, String tennv, Integer trangThai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return nhanVienRespository.findByMaAndTenAndTrangthaiNV(manv, tennv, trangThai, pageable);
    }

    // savetknhanvientodb
    public void saveSafftoDb(MultipartFile file, NhanVien nhanVien) {
        NhanVien existingNhanVien = nhanVienRespository.findById(nhanVien.getId()).orElse(null);
        if (existingNhanVien != null) {
            // Giữ nguyên giá trị ngaytao
            nhanVien.setNgaytao(existingNhanVien.getNgaytao());
            nhanVien.setNgaysua(LocalDate.now());

            // Cập nhật ảnh nếu có
            if (file != null && !file.isEmpty()) {
                try {
                    nhanVien.setImage(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Lưu đối tượng đã cập nhật
            nhanVienRespository.save(nhanVien);
        }
    }

    public void saveSafftoDbCreate(MultipartFile file, NhanVien nhanVien) {
        // Cập nhật ảnh nếu có
        if (file != null && !file.isEmpty()) {
            try {
                nhanVien.setImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lưu đối tượng đã cập nhật
        nhanVienRespository.save(nhanVien);
    }

    public NhanVien findById(Integer id) {
        return nhanVienRespository.findById(id).get();
    }

    public boolean existsNhanVienByManhanvien(String manhanvien) {
        return nhanVienRespository.existsNhanVienByManhanvien(manhanvien);
    }

    public void update(NhanVien nhanVien) {
        nhanVienRespository.save(nhanVien);
    }
}
