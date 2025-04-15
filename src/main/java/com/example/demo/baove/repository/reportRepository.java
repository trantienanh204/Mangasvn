package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Reports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface reportRepository extends JpaRepository<Reports,Integer> {
}
