package com.example.demo.respository;

import com.example.demo.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaiTroRepo extends JpaRepository<VaiTro,Integer> {
}
