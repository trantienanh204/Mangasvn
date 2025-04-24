package com.example.demo.baove.repository;

import com.example.demo.baove.entity.comics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface comicRepository extends JpaRepository<comics,Integer> {
    List<comics> findTop3ByOrderByNgayTaoDesc();
}
