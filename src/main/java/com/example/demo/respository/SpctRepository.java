package com.example.demo.respository;

import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpctRepository extends JpaRepository<SanPhamChiTiet,Integer> {
    @Query("SELECT hdct FROM SanPhamChiTiet hdct WHERE hdct.sanPham.id = :id")
    List<SanPhamChiTiet> findsanphamct(@Param("id") int id);
//    @Query("SELECT spct FROM SanPhamChiTiet spct " +
//            "JOIN spct.sanPham sp " +
//            "JOIN spct.mauSac ms " +
//            "WHERE spct.sanPham.id = :idSanPham " +
//            "AND spct.mauSac.id = :idMauSac " +
//            "AND spct.anhsanpham IS NULL")
//    List<SanPhamChiTiet> findBySanPhamAndMauSac(@Param("idSanPham") Integer idSanPham,
//                                                @Param("idMauSac") Integer idMauSac);
//
//    List<SanPhamChiTiet> findBySanPham_Id(int id);
}
