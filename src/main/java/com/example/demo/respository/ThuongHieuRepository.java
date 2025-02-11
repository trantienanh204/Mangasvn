package com.example.demo.respository;

import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.ThuongHieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu,Integer> {
    boolean existsThuongHieuByMathuonghieu(String ma);
    boolean existsThuongHieuByTenthuonghieu(String ten);

    Optional<ThuongHieu> findByTenthuonghieu(String name);

    ThuongHieu findByMathuonghieuAndIdNot(String man, Integer id);
    ThuongHieu findByTenthuonghieuAndIdNot(String ten, Integer id);

    @Query("SELECT th FROM ThuongHieu th WHERE " +
            "(?1 IS NULL OR th.tenthuonghieu LIKE %?1%) AND " +
            "(?2 IS NULL OR th.trangthai = ?2)"+
            "ORDER BY th.ngaytao DESC")
    Page<ThuongHieu> findByTenAndTrangthaiTH(String tenth, Integer trangthai, Pageable pageable);

}
