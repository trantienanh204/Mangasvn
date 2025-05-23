package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface reportRepository extends JpaRepository<Report,Integer> {
}
