package com.example.demo.baove.repository;

import com.example.demo.baove.entity.ComicDanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComicDanhMucRepository extends JpaRepository<ComicDanhMuc, Integer> {
    List<ComicDanhMuc> findByComicsId(int comicId);
    Page<ComicDanhMuc> findByDanhMucId(int danhMucId, Pageable pageable);

    @Query("SELECT cd FROM ComicDanhMuc cd WHERE cd.danhMuc.id = :danhMucId AND cd.comics.TrangThai = 1")
    Page<ComicDanhMuc> findActiveByDanhMucId(@Param("danhMucId") int danhMucId, Pageable pageable);
}
