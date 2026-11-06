package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface reportRepository extends JpaRepository<Report, Integer> {
    Page<Report> findAllByOrderByNgayTaoDesc(Pageable pageable);
    List<Report> findByComics_Id(int comicId);
    long countByTrangThai(String trangThai);
}
