package com.example.demo.baove.repository;

import com.example.demo.baove.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username); // Sửa thành username
}
