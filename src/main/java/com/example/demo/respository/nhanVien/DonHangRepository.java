package com.example.demo.respository.nhanVien;

import com.example.demo.entity.DonHang;
import com.example.demo.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {
    @Query("SELECT dh FROM DonHang dh WHERE " +
            "(?1 IS NULL OR dh.hoaDon.mahoadon LIKE %?1%) AND " +
//            "(?2 IS NULL OR dh.nhanVien.tennhanvien LIKE %?2%) AND " +
            "(?2 IS NULL OR dh.khachHang.tenkhachhang LIKE %?2%) AND " +
            "(?3 IS NULL OR dh.ngaytao >= ?3) AND " +
            "(?4 IS NULL OR dh.trangThai LIKE %?4%)")
    Page<DonHang> findByMaAndTenAndDH(String mahd
            , String tenkh
            , LocalDate Date, String trangThai, Pageable pageable);


    List<DonHang> findByKhachHangId(int khachHangId);

}
