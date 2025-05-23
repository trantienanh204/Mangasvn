package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface commentRepository extends JpaRepository<Comment,Integer> {
}
