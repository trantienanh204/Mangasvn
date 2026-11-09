package com.example.demo.baove.repository;

import com.example.demo.baove.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {
    List<TacGia> findByTenTacGiaContainingIgnoreCase(String keyword);
    boolean existsByTenTacGiaIgnoreCase(String tenTacGia);
}
