package com.example.demo.baove.controller;

import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Wishlist;
import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.repository.wishlistRepository;
import com.example.demo.baove.security.JwtUtil;
import com.example.demo.baove.service.comicService;
import com.example.demo.baove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/truyen")
public class comicController {

    @Autowired
    private comicService truyenService;

    @Autowired
    private comicRepository comicRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private wishlistRepository wishlistRepository;

    @Autowired
    private JwtUtil jwtUtil;


//    @GetMapping
//    @PreAuthorize("hasAnyRole('translator', 'admin')")
//    public ResponseEntity<?> getAllTruyen() {
//        return ResponseEntity.ok("Danh sách truyện");
//    }

    @PostMapping
    public ResponseEntity<?> createTruyen(@RequestBody TruyenRequest request) {
        String token = SecurityContextHolder.getContext().getAuthentication().getDetails().toString();
        String username = jwtUtil.getUsernameFromToken(token);
        Integer userId = userService.findByUsername(username).getId();

        Comic truyen = truyenService.createTruyen(request.getMoTa(), request.getGhiChu(), userId);
        return ResponseEntity.ok(truyen);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTruyen(@PathVariable Integer id, @RequestBody TruyenRequest request) {
        String token = SecurityContextHolder.getContext().getAuthentication().getDetails().toString();
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Integer userId = userService.findByUsername(username).getId();

        Comic truyen = truyenService.updateTruyen(id, request.getMoTa(), request.getGhiChu(), userId, role);
        return ResponseEntity.ok(truyen);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Comic>> getAllTruyen() {
        List<Comic> truyenList = comicRepository.findAll();
        return ResponseEntity.ok(truyenList);
    }

    @GetMapping("/hot")
    public ResponseEntity<List<Comic>> getHotTruyen() {
        List<Comic> hotTruyen = comicRepository.findTop5ByOrderByLuotXemDesc();
        return ResponseEntity.ok(hotTruyen);
    }
    @GetMapping("/moi")
    public ResponseEntity<List<Comic>> getMoiTruyen() {
        List<Comic> hotTruyen = comicRepository.findTop5ByOrderByNgayTaoDesc();
        return ResponseEntity.ok(hotTruyen);
    }
    @GetMapping("/favorite/status/{truyenId}")
    public ResponseEntity<Boolean> checkFavoriteStatus(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body(false);
            }

            Comic truyen = comicRepository.findById(truyenId)
                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));
            boolean isFavorited = wishlistRepository.existsByUsersAndComics(user, truyen);
            return ResponseEntity.ok(isFavorited);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(false);
        }
    }
    @PostMapping("/favorite/{truyenId}")
    public ResponseEntity<?> addToFavorite(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt);

            if (!role.equals("ROLE_user") && !role.equals("ROLE_translator") && !role.equals("ROLE_admin")) {
                return ResponseEntity.status(403).body("Bạn không có quyền thêm truyện vào danh sách yêu thích!");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("Người dùng không tồn tại!");
            }

            Comic truyen = comicRepository.findById(truyenId)
                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));

            if (wishlistRepository.existsByUsersAndComics(user, truyen)) {
                return ResponseEntity.status(200).body("Truyện đã có trong danh sách yêu thích!");
            }

            Wishlist favorite = new Wishlist();
            favorite.setUsers(user);
            favorite.setComics(truyen);
            favorite.setNgayTao(LocalDate.now());
            wishlistRepository.save(favorite);

            return ResponseEntity.ok("Thêm vào yêu thích thành công");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token không hợp lệ hoặc lỗi: " + e.getMessage());
        }
    }
    @DeleteMapping("/favorite/{truyenId}")
    public ResponseEntity<?> removeFromFavorite(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt);

            if (!role.equals("ROLE_user") && !role.equals("ROLE_translator") && !role.equals("ROLE_admin")) {
                return ResponseEntity.status(403).body("Bạn không có quyền xóa truyện khỏi danh sách yêu thích!");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("Người dùng không tồn tại!");
            }

            Comic truyen = comicRepository.findById(truyenId)
                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));

            Wishlist favorite = wishlistRepository.findByUsersAndComics(user, truyen)
                    .orElseThrow(() -> new RuntimeException("Truyện không có trong danh sách yêu thích!"));

            wishlistRepository.delete(favorite);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích!");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token không hợp lệ hoặc lỗi: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public List<Comic> searchTruyen(@RequestParam("query") String query) {
        String[] keywords = query.toLowerCase().split("\\s+");
        return comicRepository.findAll().stream()
                .filter(truyen -> {
                    for (String keyword : keywords) {
                        if (truyen.getTenTruyen().toLowerCase().contains(keyword)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public Comic getComicById(@PathVariable("id") int id) {
        return comicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Truyện với ID " + id + " không tồn tại"));
    }
}


class TruyenRequest {
    private String moTa;
    private String ghiChu;

    // Getters and setters
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
