package com.example.demo.baove.repository;


import com.example.demo.baove.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface roleRepository extends JpaRepository<Role,Integer> {
}
