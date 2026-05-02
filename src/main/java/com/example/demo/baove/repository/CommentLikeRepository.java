package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comment;
import com.example.demo.baove.entity.CommentLike;
import com.example.demo.baove.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
