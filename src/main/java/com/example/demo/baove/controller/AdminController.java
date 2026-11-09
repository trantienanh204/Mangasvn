package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.*;
import com.example.demo.baove.controller.error.messageError;
import com.example.demo.baove.controller.error.messageSuccess;
import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.entity.DanhMuc;
import com.example.demo.baove.entity.Role;
import com.example.demo.baove.entity.TacGia;
import com.example.demo.baove.repository.RoleRepository;
import com.example.demo.baove.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AdminService adminService;

    /**
     * Lấy danh sách các vai trò (roles) trong hệ thống
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    /**
     * Thống kê tổng quan hệ thống (Số truyện, chapter, user, comment, view, report)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    /**
     * Lấy danh sách người dùng (hỗ trợ tìm kiếm, lọc theo vai trò, lọc trạng thái)
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserAdminDTO>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(adminService.getUsers(keyword, roleId, trangThai, page, size));
    }

    /**
     * Khóa hoặc mở khóa tài khoản người dùng
     */
    @PutMapping("/users/{id}/toggle-ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserBan(@PathVariable int id, Principal principal) {
        try {
            UserAdminDTO updated = adminService.toggleUserBan(id, principal.getName());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Phân quyền cho người dùng (nâng cấp CHUTUT hoặc hạ về DOCGIA)
     */
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable int id,
            @RequestBody RoleUpdateDTO request,
            Principal principal) {
        try {
            UserAdminDTO updated = adminService.updateUserRole(id, request.getRoleId(), principal.getName());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Lấy danh sách truyện phía admin & chủ thớt:
     * - CHUTUT: Chỉ lấy truyện của chính mình.
     * - ADMIN: Lấy tất cả truyện.
     * - Bộ lọc: từ khóa (tên truyện/tác giả/người đăng), thể loại, tác giả, trạng thái.
     */
    @GetMapping("/comics")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<Page<ComicAdminDTO>> getComics(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Byte trangThai,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(adminService.getComics(
                keyword, trangThai, categoryId, authorId, page, size, username, isAdmin));
    }

    /**
     * Ẩn hoặc Hiện truyện (Toggle trạng thái giữa 1 và 0)
     */
    @PutMapping("/comics/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<?> toggleComicStatus(
            @PathVariable int id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            ComicAdminDTO comic = adminService.toggleComicStatus(id, username, isAdmin);
            return ResponseEntity.ok(comic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Xóa mềm truyện (chuyển sang trạng thái -1)
     */
    @DeleteMapping("/comics/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<?> softDeleteComic(
            @PathVariable int id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            ComicAdminDTO comic = adminService.softDeleteComic(id, username, isAdmin);
            return ResponseEntity.ok(new messageSuccess("Đã xóa mềm truyện ID " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Xóa một chapter cụ thể
     */
    @DeleteMapping("/chapters/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<?> deleteChapter(
            @PathVariable int id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            adminService.deleteChapter(id, username, isAdmin);
            return ResponseEntity.ok(new messageSuccess("Đã xóa thành công chapter ID " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Xóa bình luận vi phạm
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable int id) {
        try {
            adminService.deleteComment(id);
            return ResponseEntity.ok(new messageSuccess("Đã xóa bình luận ID " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Lấy danh sách báo cáo lỗi từ người dùng
     */
    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(adminService.getReports(page, size));
    }

    /**
     * Cập nhật trạng thái xử lý báo cáo
     */
    @PutMapping("/reports/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(new messageError("Trạng thái không được để trống"));
        }
        try {
            ReportDTO updated = adminService.updateReportStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    /**
     * Xóa báo cáo lỗi
     */
    @DeleteMapping("/reports/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteReport(@PathVariable int id) {
        try {
            adminService.deleteReport(id);
            return ResponseEntity.ok(new messageSuccess("Đã xóa báo cáo ID " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    // ==========================================
    // QUẢN LÝ THỂ LOẠI (DANH MỤC)
    // ==========================================

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DanhMuc>> getCategories() {
        return ResponseEntity.ok(adminService.getAllCategoriesForAdmin());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDTO request) {
        try {
            DanhMuc created = adminService.createCategory(request.getTenDanhMuc(), request.getNoiDung());
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestBody CategoryRequestDTO request) {
        try {
            DanhMuc updated = adminService.updateCategory(id, request.getTenDanhMuc(), request.getNoiDung());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable int id) {
        try {
            DanhMuc updated = adminService.toggleCategoryStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        try {
            adminService.deleteCategory(id);
            return ResponseEntity.ok(new messageSuccess("Đã xóa thể loại ID " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    // ==========================================
    // QUẢN LÝ TÁC GIẢ (AUTHORS)
    // ==========================================

    @GetMapping("/authors")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<List<TacGia>> getAuthors(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(adminService.getAllAuthorsForAdmin(keyword));
    }

    @PostMapping("/authors")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<?> createAuthor(@RequestBody AuthorRequestDTO request) {
        try {
            TacGia created = adminService.createAuthor(request);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    @PutMapping("/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAuthor(@PathVariable int id, @RequestBody AuthorRequestDTO request) {
        try {
            TacGia updated = adminService.updateAuthor(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    @PutMapping("/authors/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleAuthorStatus(@PathVariable int id) {
        try {
            TacGia updated = adminService.toggleAuthorStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }

    @DeleteMapping("/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAuthor(@PathVariable int id) {
        try {
            adminService.deleteAuthor(id);
            return ResponseEntity.ok(new messageSuccess("Đã xóa tác giả ID " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new messageError(e.getMessage()));
        }
    }
}
