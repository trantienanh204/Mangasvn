package com.example.demo.baove.repository;

import com.example.demo.baove.entity.ComicDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comic_danhmucRepository extends JpaRepository<ComicDanhMuc,Integer> {
}
