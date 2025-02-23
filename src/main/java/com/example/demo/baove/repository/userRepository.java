package com.example.demo.baove.repository;

import com.example.demo.baove.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public interface userRepository extends JpaRepository<user,Integer> {
}
