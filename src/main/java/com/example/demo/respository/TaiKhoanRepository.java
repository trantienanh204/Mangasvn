package com.example.demo.respository;

import com.example.demo.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan,String> {
    TaiKhoan findByTenDangNhap(String ten);
    boolean existsByTenDangNhap(String tenTaiKhoan);
}
