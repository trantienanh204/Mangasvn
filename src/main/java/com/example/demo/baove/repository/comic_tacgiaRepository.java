package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comic_TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface comic_tacgiaRepository extends JpaRepository<Comic_TacGia,Integer>
{
}
