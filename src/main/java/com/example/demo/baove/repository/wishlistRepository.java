package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface wishlistRepository extends JpaRepository<Wishlist,Integer> {
}
