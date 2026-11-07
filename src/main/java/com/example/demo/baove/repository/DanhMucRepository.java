package com.example.demo.baove.repository;

import com.example.demo.baove.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    DanhMuc findByTenDanhMuc(String genre);
    DanhMuc findByTenDanhMucIgnoreCase(String genre);
    Optional<DanhMuc> findById(int id);
    List<DanhMuc> findByTrangThaiTrue();
}
