package com.example.demo.baove.repository;

import com.example.demo.baove.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface tacgiaRepository extends JpaRepository<TacGia,Integer> {
}
