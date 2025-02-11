package com.example.demo.respository;

import com.example.demo.entity.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc,Integer> {
    boolean existsDanhMucByMadanhmuc(String ma);
    boolean existsDanhMucByTendanhmuc(String ten);

    DanhMuc findByMadanhmucAndIdNot(String ma, Integer id);
    DanhMuc findByTendanhmucAndIdNot(String ten, Integer id);

    @Query("SELECT dm FROM DanhMuc dm WHERE " +
            "(?1 IS NULL OR dm.tendanhmuc LIKE %?1%) AND " +
            "(?2 IS NULL OR dm.trangthai = ?2)"+
            "ORDER BY dm.ngaytao DESC")
    Page<DanhMuc> findByTenAndTrangthaiDM(String tendm, Integer trangthai, Pageable pageable);

}
