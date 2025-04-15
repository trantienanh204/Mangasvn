package com.example.demo.baove.repository;

import com.example.demo.baove.entity.comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface commentRepository extends JpaRepository<comments,Integer> {
}
