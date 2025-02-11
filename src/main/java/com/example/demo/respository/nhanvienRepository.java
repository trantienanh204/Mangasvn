package com.example.demo.respository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface nhanvienRepository extends JpaRepository<NhanVien, Long> {
    Optional<NhanVien> findByTentaikhoan(String tentaikhoan);  // Đảm bảo trả về Optional

}

