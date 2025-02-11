package com.example.demo.respository.nhanVien;

import com.example.demo.entity.GioHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    Optional<GioHang> findByKhachHangAndSanPhamChiTiet(KhachHang khachHang, SanPhamChiTiet sanPhamChiTiet);
    List<GioHang> findByKhachHangId(Integer khachHangId);

    GioHang findByKhachHangIdAndId(Integer khachHangId, Integer sanPhamChiTietId);

    @Query("SELECT g FROM GioHang g WHERE g.id IN :ids AND g.khachHang.id = :khachHangId")
    List<GioHang> findAllByIdInAndKhachHangId(@Param("ids") List<Integer> ids, @Param("khachHangId") int khachHangId);
}



