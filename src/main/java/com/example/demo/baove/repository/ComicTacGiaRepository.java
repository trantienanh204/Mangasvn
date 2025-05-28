package com.example.demo.baove.repository;

import com.example.demo.baove.entity.ComicTacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComicTacGiaRepository extends JpaRepository<ComicTacGia,Integer>
{
    List<ComicTacGia> findByComicsId(int comicId);
}
