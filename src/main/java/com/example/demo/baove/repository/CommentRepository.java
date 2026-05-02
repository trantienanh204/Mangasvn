package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findByChapters_IdOrderByNgayTaoDesc(int chapterId);
    List<Comment> findByComics_IdAndChaptersIsNullOrderByNgayTaoDesc(int comicId);


    List<Comment> findByChapters_IdAndParentCommentIsNullOrderByNgayTaoDesc(int chapterId);

    List<Comment> findByComics_IdAndChaptersIsNullAndParentCommentIsNullOrderByNgayTaoDesc(int comicId);
}
