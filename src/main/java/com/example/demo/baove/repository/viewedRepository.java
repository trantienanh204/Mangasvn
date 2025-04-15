package com.example.demo.baove.repository;

import com.example.demo.baove.entity.viewed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface viewedRepository extends JpaRepository<viewed,Integer> {
}
