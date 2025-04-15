package com.example.demo.baove.repository;

import com.example.demo.baove.entity.comics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comicRepository extends JpaRepository<comics,Integer> {
}
