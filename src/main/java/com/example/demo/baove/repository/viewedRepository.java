package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Viewed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface viewedRepository extends JpaRepository<Viewed,Integer> {
}
