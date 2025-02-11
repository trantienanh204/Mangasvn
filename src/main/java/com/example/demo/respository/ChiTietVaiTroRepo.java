package com.example.demo.respository;

import com.example.demo.entity.ChiTietVaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietVaiTroRepo extends JpaRepository<ChiTietVaiTro,Integer> {
}
