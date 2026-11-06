package com.example.demo.baove.repository;

import com.example.demo.baove.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findById(int id);

    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:roleId IS NULL OR u.role.id = :roleId) " +
           "AND (:trangThai IS NULL OR u.trangThai = :trangThai)")
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("roleId") Integer roleId,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);
}
