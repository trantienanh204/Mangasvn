package com.example.demo.respository;


import com.example.demo.entity.MauSac;
import com.example.demo.entity.NhaSanXuat;
import com.example.demo.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MauSacRepsitory extends JpaRepository<MauSac,Integer> {
    List<MauSac> findAll();
    boolean existsMauSacByMa(String ma);
    boolean existsMauSacByTen(String ten);

    MauSac findByMaAndIdNot(String ma, Integer id);
    MauSac findByTenAndIdNot(String ten, Integer id);

    Optional<MauSac> findByTen(String name);

    @Query("SELECT ms FROM MauSac ms WHERE " +
            "(?1 IS NULL OR ms.ten LIKE %?1%) AND " +
            "(?2 IS NULL OR ms.trangthai = ?2)"+
            "ORDER BY ms.ngaytao DESC")
    Page<MauSac> findByTenAndTrangthai(String tenms, Integer trangthai, Pageable pageable);

}
