package com.example.demo.respository;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRespository extends JpaRepository<NhanVien,Integer> {
    boolean existsNhanVienByManhanvien(String manhanvien);



    @Query("SELECT nv FROM NhanVien nv WHERE " +
            "(?1 IS NULL OR nv.manhanvien LIKE %?1%) AND " +
            "(?2 IS NULL OR nv.tennhanvien LIKE %?2%) AND " +
            "(?3 IS NULL OR nv.trangthai = ?3)"+
            "ORDER BY nv.ngaytao DESC")
    Page<NhanVien> findByMaAndTenAndTrangthaiNV(String manv, String tennv, Integer trangThai, Pageable pageable);



//    NhanVien findByTentaikhoan(String name);
//
//    @Query("SELECT nv FROM NhanVien nv LEFT JOIN FETCH nv.userRoles WHERE nv.tentaikhoan = :taiKhoan")
//    NhanVien findByTenTaiKhoanWithRoles(@Param("taiKhoan") String taiKhoan);

    boolean existsByTentaikhoan(String tentaikhoan);
    boolean existsByTentaikhoanAndId(String tentaikhoan,Integer id);


}
