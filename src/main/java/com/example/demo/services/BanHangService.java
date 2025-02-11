package com.example.demo.services;

import com.example.demo.DTO.HoaDonCTDTO;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.respository.HoaDonCTRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BanHangService {
    @Autowired
    HoaDonCTRepository hoaDonCTRepository;
    @PersistenceContext
    private EntityManager entityManager;

    // Phương thức tìm kiếm sản phẩm
    public List<SanPhamChiTiet> findByTenSanPham(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String queryString = "SELECT sp FROM SanPhamChiTiet sp WHERE " +
                "LOWER(sp.sanPham.tensp) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(sp.sanPham.masp) LIKE LOWER(CONCAT('%', :keyword, '%'))";
        TypedQuery<SanPhamChiTiet> query = entityManager.createQuery(queryString, SanPhamChiTiet.class);
        query.setParameter("keyword", keyword.trim());

        return query.getResultList();
    }

    public List<KhachHang> findBysdt(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String queryString = "SELECT kh FROM KhachHang kh WHERE " +

//                "LOWER(kh.makhachhang) LIKE LOWER(CONCAT('%', :keyword, '%'))  OR " +

                "LOWER(kh.sdt) LIKE LOWER(CONCAT('%', :keyword, '%'))";
        TypedQuery<KhachHang> query = entityManager.createQuery(queryString, KhachHang.class);
        query.setParameter("keyword", keyword.trim());
        return query.getResultList();
    }

    @Transactional
    public void save(HoaDonCTDTO DTO) {
        HoaDonCT hoaDonCT = new HoaDonCT();
        hoaDonCT.setHoaDon(new HoaDon(DTO.getIdHoadon()));
        hoaDonCT.setSanPhamChiTiet(new SanPhamChiTiet(DTO.getIdSanPhamCT()));
        hoaDonCT.setDongia(DTO.getDonGia());
        hoaDonCT.setSoluong(DTO.getSoLuong());
        hoaDonCT.setTongtien(DTO.getThanhTien());
        // Lưu hóa đơn chi tiết vào cơ sở dữ liệu
        hoaDonCTRepository.save(hoaDonCT);
    }

    public BigDecimal getTongTienByHoaDonId(Integer idHoaDon) {
        return hoaDonCTRepository.TongTienByHoaDonId(idHoaDon);
    }


}
