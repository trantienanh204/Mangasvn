package com.example.demo.baove.repository;

import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Wishlist;
import com.example.demo.baove.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface wishlistRepository extends JpaRepository<Wishlist,Integer> {
    boolean existsByUsersAndComics(User users, Comic comics);
    Optional<Wishlist> findByUsersAndComics(User users, Comic comics);
    List<Wishlist> findByUsers(User user);
}
