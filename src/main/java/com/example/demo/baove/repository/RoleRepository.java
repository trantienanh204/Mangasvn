package com.example.demo.baove.repository;


import com.example.demo.baove.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByRoleName(String roleName);
    Role findById (int  id);
}