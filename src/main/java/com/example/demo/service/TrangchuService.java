package com.example.demo.service;

import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.respository.SanPhamChiTietRepository;
import com.example.demo.respository.SanPhamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrangchuService {
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    SanPhamRepository sanPhamRepository;
    public List<SanPhamChiTiet> test(){
        return sanPhamChiTietRepository.findAll();
    }
    public List<SanPham> spfinall(){ List<SanPham> sanphamList = sanPhamRepository.findSanPhamWithDetails();
    for(SanPham sp : sanphamList) { System.out.println("Dữ liệu ảnh base64: " + sp.getAnhsp()); } return sanphamList; }

    public List<SanPhamChiTiet> timkiemspct(Integer id){
        return sanPhamChiTietRepository.findBySanPhamId(id);
    }
}
