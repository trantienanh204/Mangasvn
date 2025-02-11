package com.example.demo.respository;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.CoAo;
import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.NhaSanXuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NSXRepository extends JpaRepository<NhaSanXuat,Integer> {
    boolean existsNhaSanXuatByMansx(String ma);
    boolean existsNhaSanXuatByTennsx(String ten);

    Optional<NhaSanXuat> findByTennsx(String name);
    NhaSanXuat findByMansxAndIdNot(String mansx, Integer id);
    NhaSanXuat findByTennsxAndIdNot(String tennsx, Integer id);

    @Query("SELECT nsx FROM NhaSanXuat nsx WHERE " +
            "(?1 IS NULL OR nsx.tennsx LIKE %?1%) AND " +
            "(?2 IS NULL OR nsx.trangthai = ?2)")
    Page<NhaSanXuat> findByTenAndTrangthaiNSX(String tennsx, Integer trangthai, Pageable pageable);

}
