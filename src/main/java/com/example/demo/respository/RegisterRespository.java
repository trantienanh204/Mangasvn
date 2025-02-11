package com.example.demo.respository;

import com.example.demo.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
@Repository

public interface RegisterRespository extends JpaRepository<KhachHang,Integer> {
}
