package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comic_DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comic_danhmucRepository extends JpaRepository<Comic_DanhMuc,Integer> {
}
