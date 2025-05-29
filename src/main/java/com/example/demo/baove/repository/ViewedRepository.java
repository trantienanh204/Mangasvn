package com.example.demo.baove.repository;

import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Viewed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewedRepository extends JpaRepository<Viewed, Long> {
    List<Viewed> findByUsers(User user);
    void deleteByUsers(User user);
    void deleteByUsersAndComicsId(User user, Long comicId);
    Viewed findByUsersAndComicsId(User user, int comicId);
}