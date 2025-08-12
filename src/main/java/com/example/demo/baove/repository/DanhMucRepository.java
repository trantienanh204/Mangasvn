package com.example.demo.baove.repository;

import com.example.demo.baove.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc,Integer> {
    public DanhMuc findByTenDanhMuc (String genre);
}
