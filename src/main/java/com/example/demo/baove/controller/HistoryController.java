package com.example.demo.baove.controller;

import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Viewed;
import com.example.demo.baove.repository.UserRepository;
import com.example.demo.baove.service.ViewedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ViewedService viewedService;

    @GetMapping
    public ResponseEntity<List<HistoryResponse>> getHistory(Principal principal) {
        if (principal == null) {
            logger.error("Không tìm thấy thông tin user từ token.");
            return ResponseEntity.status(401).body(null);
        }

        System.out.println("✅ Principal username: " + principal.getName());

        List<Viewed> history = viewedService.getViewedByUser(userRepository.findByUsername(principal.getName()));
        return ResponseEntity.ok(history.stream().map(viewed -> {
            HistoryResponse hr = new HistoryResponse();
            hr.setComicId(viewed.getComics().getId());
            hr.setTenTruyen(viewed.getComics().getTenTruyen());
            hr.setImageComic(viewed.getComics().getImageComic());
            hr.setNgayTao(viewed.getNgayTao().toString());
            return hr;
        }).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> saveHistory(
            Principal principal,
            @RequestBody HistoryRequest request) {
        if (principal == null) {
            logger.error("Không tìm thấy thông tin user từ token.");
            return ResponseEntity.status(401).body(null);
        }

        User user = userRepository.findByUsername(principal.getName());
        int comicId =  request.getComicId(); // Chuyển int sang Long để khớp với entity

        // Kiểm tra xem bản ghi đã tồn tại chưa
        Viewed existingViewed = viewedService.findByUserAndComicId(user, comicId);
        if (existingViewed != null) {
            // Nếu đã tồn tại, cập nhật ngay_tao
            existingViewed.setNgayTao(LocalDateTime.now());
            viewedService.saveViewed(existingViewed);
            logger.info("Cập nhật lịch sử cho user: {} và comic: {}", user.getUsername(), comicId);
            return ResponseEntity.ok("Cập nhật lịch sử thành công");
        } else {
            // Nếu chưa tồn tại, tạo mới
            com.example.demo.baove.entity.Comic comic = new com.example.demo.baove.entity.Comic();
            comic.setId(comicId); // Chỉ set ID, các trường khác sẽ được tải khi cần
            Viewed newViewed = new Viewed();
            newViewed.setUsers(user);
            newViewed.setComics(comic);
            newViewed.setNgayTao(LocalDateTime.now());
            viewedService.saveViewed(newViewed);
            logger.info("Lưu mới lịch sử cho user: {} và comic: {}", user.getUsername(), comicId);
            return ResponseEntity.ok("Lưu lịch sử thành công");
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHistory(Principal principal) {
        if (principal == null) {
            logger.error("Không tìm thấy thông tin user từ token.");
            return ResponseEntity.status(401).body(null);
        }

        viewedService.deleteViewedByUser(userRepository.findByUsername(principal.getName()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{comicId}")
    public ResponseEntity<Void> deleteHistoryByComicId(
            Principal principal,
            @PathVariable Long comicId) {
        if (principal == null) {
            logger.error("Không tìm thấy thông tin user từ token.");
            return ResponseEntity.status(401).body(null);
        }

        viewedService.deleteViewedByUserAndComicId(userRepository.findByUsername(principal.getName()), comicId);
        return ResponseEntity.noContent().build();
    }
}

class HistoryRequest {
    private int comicId;

    public int getComicId() {
        return comicId;
    }

    public void setComicId(int comicId) {
        this.comicId = comicId;
    }
}

class HistoryResponse {
    private int comicId;
    private String tenTruyen;
    private String imageComic;
    private String ngayTao;

    public int getComicId() {
        return comicId;
    }

    public void setComicId(int comicId) {
        this.comicId = comicId;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getImageComic() {
        return imageComic;
    }

    public void setImageComic(String imageComic) {
        this.imageComic = imageComic;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}