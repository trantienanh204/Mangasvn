package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.CommentDTO;
import com.example.demo.baove.entity.*;
import com.example.demo.baove.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Dùng RestController

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ComicRepository comicRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<Comment>> getCommentsByChapter(@PathVariable int chapterId) {
        return ResponseEntity.ok(commentRepository.findByChapters_IdOrderByNgayTaoDesc(chapterId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDTO request, Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Chưa xác thực Token");
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        Comic comic = comicRepository.findById(request.getComicId()).orElse(null);

        if (user == null || comic == null) {
            return ResponseEntity.badRequest().body("Người dùng hoặc truyện không tồn tại");
        }
        Comment comment = new Comment();
        comment.setUsers(user);
        comment.setComics(comic);
        comment.setComment(request.getContent());
        comment.setLike(0);
        comment.setTrangThai(1);
        comment.setNgayTao(LocalDate.now());

        if (request.getParentId() != null && request.getParentId() > 0) {
            Comment parent = commentRepository.findById(request.getParentId()).orElse(null);
            comment.setParentComment(parent);
        }

        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);


    }
    @GetMapping("/comic/{comicId}")
    public ResponseEntity<List<Comment>> getCommentsByComic(@PathVariable int comicId) {
        return ResponseEntity.ok(commentRepository.findByComics_IdAndChaptersIsNullOrderByNgayTaoDesc(comicId));
    }
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> toggleLikeComment(@PathVariable int commentId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập");
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (user == null || comment == null) {
            return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
        }

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);

        if (existingLike.isPresent()) {
           commentLikeRepository.delete(existingLike.get());
            comment.setLike(comment.getLike() - 1);
            commentRepository.save(comment);

            return ResponseEntity.ok("UNLIKED");
        } else {

            CommentLike newLike = new CommentLike();
            newLike.setComment(comment);
            newLike.setUser(user);
            commentLikeRepository.save(newLike);

            comment.setLike(comment.getLike() + 1);
            commentRepository.save(comment);

            return ResponseEntity.ok("LIKED");
        }
    }
}