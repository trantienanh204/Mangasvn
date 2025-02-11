package com.example.demo.service;

import com.example.demo.entity.HoaDon;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.respository.HoaDonRepository;
import com.example.demo.respository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    private final int size = 5;


    public Page<HoaDon> hienThiHD(int page, String mahd,String nv,  String tenkh, Integer trangThai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);



        return hoaDonRepository.findByMaAndTenAndTrangthaiHD(mahd,nv,  tenkh, trangThai, pageable);

    }


    public HoaDon findById(Integer id) {
        return hoaDonRepository.findById(id).get();
    }
    public void save(HoaDon hoaDon){
        hoaDonRepository.save(hoaDon);
    }

}
