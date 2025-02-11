package com.example.demo.respository;

import com.example.demo.entity.Tinh_TP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TinhTPRepo extends JpaRepository<Tinh_TP,Integer> {
}
