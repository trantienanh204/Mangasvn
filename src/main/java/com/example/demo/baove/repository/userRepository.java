package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface userRepository extends JpaRepository<Users,Integer> {
    Users findByUserName(String userName);
}
