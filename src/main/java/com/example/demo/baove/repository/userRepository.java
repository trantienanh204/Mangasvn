package com.example.demo.baove.repository;

import com.example.demo.baove.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface userRepository extends JpaRepository<user,Integer> {
}
