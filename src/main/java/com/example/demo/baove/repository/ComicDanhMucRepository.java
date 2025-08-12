package com.example.demo.baove.repository;

import com.example.demo.baove.entity.ComicDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComicDanhMucRepository extends JpaRepository<ComicDanhMuc,Integer> {
    List<ComicDanhMuc> findByComicsId(int comicId);
    List<ComicDanhMuc> findByDanhMucId(int comicId);
}
