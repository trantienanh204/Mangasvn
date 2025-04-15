package com.example.demo.baove.repository;

import com.example.demo.baove.entity.chapters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface chapterRepository extends JpaRepository<chapters,Integer> {
}
