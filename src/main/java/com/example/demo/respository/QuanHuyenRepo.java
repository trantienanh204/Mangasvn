package com.example.demo.respository;

import com.example.demo.entity.QuanHuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuanHuyenRepo extends JpaRepository<QuanHuyen,Integer> {
    @Query("SELECT qh FROM QuanHuyen qh WHERE qh.tinh_tp.id = :provinceId")
    List<QuanHuyen> findByProvinceId(@Param("provinceId") Integer provinceId);
}
