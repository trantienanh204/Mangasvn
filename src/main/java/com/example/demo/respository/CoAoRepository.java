package com.example.demo.respository;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.CoAo;
import com.example.demo.entity.NhaSanXuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoAoRepository extends JpaRepository<CoAo,Integer> {
    boolean existsCoAoByMa(String ma);
    boolean existsCoAoByTen(String ten);
    Optional<CoAo> findByTen(String name);

    @Query("SELECT ca FROM CoAo ca WHERE " +
            "(?1 IS NULL OR ca.ten LIKE %?1%) AND " +
            "(?2 IS NULL OR ca.trangThai = ?2)"+
            "ORDER BY ca.ngayTao DESC")
    Page<CoAo> findByTenAndTrangthaiCA(String tenca, Integer trangThai, Pageable pageable);

}
