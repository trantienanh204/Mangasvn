package com.example.demo.respository;

import com.example.demo.entity.CoAo;
import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc,Integer> {
    boolean existsKichThuocByMa(String ma);
    boolean existsKichThuocByTen(String ten);
    Optional<KichThuoc> findByten(String name);
    KichThuoc findByMaAndIdNot(String ma, Integer id);
    KichThuoc findByTenAndIdNot(String ten, Integer id);

    @Query("SELECT kt FROM KichThuoc kt WHERE " +
            "(?1 IS NULL OR kt.ten LIKE %?1%) AND " +
            "(?2 IS NULL OR kt.trangthai = ?2)"+
            "ORDER BY kt.ngaytao DESC")
    Page<KichThuoc> findByTenAndTrangthaiKT(String tenms, Integer trangthai, Pageable pageable);

}
