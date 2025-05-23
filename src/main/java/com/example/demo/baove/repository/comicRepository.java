package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface comicRepository extends JpaRepository<Comic,Integer> {
    List<Comic> findTop5ByOrderByNgayTaoDesc();
    List<Comic> findTop5ByOrderByLuotXemDesc();
}
