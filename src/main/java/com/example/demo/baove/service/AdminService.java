package com.example.demo.baove.service;

import com.example.demo.baove.controller.DTO.AdminStatsDTO;
import com.example.demo.baove.controller.DTO.ComicAdminDTO;
import com.example.demo.baove.controller.DTO.ReportDTO;
import com.example.demo.baove.controller.DTO.UserAdminDTO;
import com.example.demo.baove.entity.*;
import com.example.demo.baove.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminService {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private reportRepository reportRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ImageChapterRepository imageChapterRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ComicDanhMucRepository comicDanhMucRepository;

    /**
     * Thống kê tổng quan hệ thống cho Admin Dashboard
     */
    @Transactional(readOnly = true)
    public AdminStatsDTO getStats() {
        long totalComics = comicRepository.count();
        long totalChapters = chapterRepository.count();
        long totalUsers = userRepository.count();
        long totalComments = commentRepository.count();
        long totalViews = comicRepository.sumTotalViews();
        long totalReports = reportRepo.count();

        return new AdminStatsDTO(totalComics, totalChapters, totalUsers, totalComments, totalViews, totalReports);
    }

    /**
     * Lấy danh sách người dùng có tìm kiếm và lọc
     */
    @Transactional(readOnly = true)
    public Page<UserAdminDTO> getUsers(String keyword, Integer roleId, Boolean trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> userPage = userRepository.searchUsers(keyword, roleId, trangThai, pageable);
        return userPage.map(this::toUserAdminDTO);
    }

    /**
     * Khóa hoặc mở khóa người dùng (Ban / Unban)
     */
    @Transactional
    public UserAdminDTO toggleUserBan(int userId, String currentAdminUsername) {
        User target = userRepository.findById(userId);
        if (target == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId);
        }

        if (target.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("Bạn không thể tự khóa tài khoản của chính mình!");
        }

        if (target.getRole() != null && "ADMIN".equalsIgnoreCase(target.getRole().getRoleName())) {
            throw new IllegalArgumentException("Không thể khóa tài khoản có quyền ADMIN khác!");
        }

        target.setTrangThai(!target.isTrangThai());
        userRepository.save(target);
        log.info("Admin {} vừa thay đổi trạng thái user {} thành {}", currentAdminUsername, target.getUsername(), target.isTrangThai());
        return toUserAdminDTO(target);
    }

    /**
     * Cập nhật vai trò người dùng (phân quyền)
     */
    @Transactional
    public UserAdminDTO updateUserRole(int userId, int newRoleId, String currentAdminUsername) {
        User target = userRepository.findById(userId);
        if (target == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId);
        }

        if (target.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("Bạn không thể tự thay đổi quyền của chính mình!");
        }

        Role newRole = roleRepository.findById(newRoleId);
        if (newRole == null) {
            throw new IllegalArgumentException("Vai trò không hợp lệ với ID: " + newRoleId);
        }

        target.setRole(newRole);
        userRepository.save(target);
        log.info("Admin {} vừa cập nhật quyền user {} thành {}", currentAdminUsername, target.getUsername(), newRole.getRoleName());
        return toUserAdminDTO(target);
    }

    /**
     * Lấy danh sách truyện nâng cao:
     * - Nếu là CHUTUT: Chỉ lấy danh sách truyện do chính mình đăng.
     * - Nếu là ADMIN: Lấy tất cả truyện (có thể lọc theo người đăng, thể loại, tác giả, trạng thái, keyword).
     */
    @Transactional(readOnly = true)
    public Page<ComicAdminDTO> getComics(
            String keyword,
            Byte trangThai,
            Integer categoryId,
            Integer authorId,
            int page,
            int size,
            String currentUsername,
            boolean isAdmin) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Phân quyền chặt chẽ: nếu không phải ADMIN, bắt buộc chỉ lọc truyện của chính CHUTUT đó
        String filterTranslator = isAdmin ? null : currentUsername;

        Page<Comic> comicPage = comicRepository.searchComicsAdvanced(
                filterTranslator,
                keyword,
                trangThai,
                categoryId,
                authorId,
                pageable);

        return comicPage.map(c -> toComicAdminDTO(c, currentUsername, isAdmin));
    }

    /**
     * Ẩn hoặc Hiện truyện (Toggle status giữa 1 và 0):
     * - CHUTUT: Chỉ có quyền ẩn/hiện truyện do mình tạo.
     * - ADMIN: Toàn quyền với mọi truyện.
     */
    @Transactional
    public ComicAdminDTO toggleComicStatus(int comicId, String currentUsername, boolean isAdmin) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy truyện với ID: " + comicId));

        // Kiểm tra quyền sở hữu
        if (!isAdmin) {
            if (comic.getTranslator() == null || !comic.getTranslator().getUsername().equalsIgnoreCase(currentUsername)) {
                throw new IllegalArgumentException("Bạn không có quyền thay đổi trạng thái truyện của người khác!");
            }
        }

        byte current = comic.getTrangThai();
        byte next = (current == 1) ? (byte) 0 : (byte) 1;
        comic.setTrangThai(next);
        comic.setNgaySua(LocalDate.now());
        comicRepository.save(comic);
        log.info("User {} thay đổi trạng thái truyện ID {} từ {} sang {}", currentUsername, comicId, current, next);
        return toComicAdminDTO(comic, currentUsername, isAdmin);
    }

    /**
     * Xóa mềm truyện (chuyển trạng thái sang -1):
     * - CHUTUT: Chỉ có quyền xóa truyện do mình tạo.
     * - ADMIN: Toàn quyền xóa mọi truyện.
     */
    @Transactional
    public ComicAdminDTO softDeleteComic(int comicId, String currentUsername, boolean isAdmin) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy truyện với ID: " + comicId));

        if (!isAdmin) {
            if (comic.getTranslator() == null || !comic.getTranslator().getUsername().equalsIgnoreCase(currentUsername)) {
                throw new IllegalArgumentException("Bạn không có quyền xóa truyện của người khác!");
            }
        }

        comic.setTrangThai((byte) -1);
        comic.setNgaySua(LocalDate.now());
        comicRepository.save(comic);
        log.info("User {} đã xóa mềm truyện ID {}", currentUsername, comicId);
        return toComicAdminDTO(comic, currentUsername, isAdmin);
    }

    /**
     * Xóa hẳn một chapter cụ thể:
     * - CHUTUT: Chỉ xóa chapter của truyện do chính mình tạo.
     * - ADMIN: Toàn quyền.
     */
    @Transactional
    public void deleteChapter(int chapterId, String currentUsername, boolean isAdmin) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chapter với ID: " + chapterId));

        Comic comic = chapter.getComic();
        if (comic == null) {
            comic = comicRepository.findById(chapter.getIdComic()).orElse(null);
        }

        if (!isAdmin && comic != null) {
            if (comic.getTranslator() == null || !comic.getTranslator().getUsername().equalsIgnoreCase(currentUsername)) {
                throw new IllegalArgumentException("Bạn không có quyền xóa chapter của truyện người khác!");
            }
        }

        // 1. Xóa bình luận liên quan đến chapter
        commentRepository.deleteByChapters_Id(chapterId);

        // 2. Xóa ảnh thuộc chapter
        imageChapterRepository.deleteByChapterId(chapterId);

        // 3. Xóa chapter
        chapterRepository.delete(chapter);
        log.info("User {} đã xóa thành công chapter ID {}", currentUsername, chapterId);
    }

    /**
     * Xóa bình luận vi phạm
     */
    @Transactional
    public void deleteComment(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận với ID: " + commentId));

        commentLikeRepository.deleteByComment(comment);
        commentRepository.delete(comment);
        log.info("Đã xóa bình luận vi phạm ID {}", commentId);
    }

    /**
     * Lấy danh sách báo cáo lỗi từ người dùng
     */
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reports = reportRepo.findAllByOrderByNgayTaoDesc(pageable);
        return reports.map(this::toReportDTO);
    }

    /**
     * Cập nhật trạng thái báo cáo
     */
    @Transactional
    public ReportDTO updateReportStatus(int reportId, String status) {
        Report report = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy báo cáo với ID: " + reportId));

        report.setTrangThai(status);
        reportRepo.save(report);
        log.info("Đã cập nhật trạng thái report ID {} thành {}", reportId, status);
        return toReportDTO(report);
    }

    /**
     * Xóa báo cáo lỗi
     */
    @Transactional
    public void deleteReport(int reportId) {
        if (!reportRepo.existsById(reportId)) {
            throw new IllegalArgumentException("Không tìm thấy báo cáo với ID: " + reportId);
        }
        reportRepo.deleteById(reportId);
        log.info("Đã xóa báo cáo ID {}", reportId);
    }

    /**
     * Tạo báo cáo lỗi từ người dùng
     */
    @Transactional
    public ReportDTO createReport(Integer comicId, String lyDo, String username) {
        User user = null;
        if (username != null && !username.isBlank()) {
            user = userRepository.findByUsername(username);
        }

        Comic comic = null;
        if (comicId != null) {
            comic = comicRepository.findById(comicId).orElse(null);
        }

        Report report = new Report();
        report.setUsers(user);
        report.setComics(comic);
        report.setLyDo(lyDo);
        report.setTrangThai("CHUA_XU_LY");
        report.setNgayTao(LocalDate.now());

        reportRepo.save(report);
        log.info("Đã tạo báo cáo mới từ user {} cho truyện ID {}", username, comicId);
        return toReportDTO(report);
    }

    // ==========================================
    // QUẢN LÝ THỂ LOẠI (DANH MỤC)
    // ==========================================

    @Transactional(readOnly = true)
    public List<DanhMuc> getAllCategoriesForAdmin() {
        return danhMucRepository.findAll();
    }

    @Transactional
    public DanhMuc createCategory(String tenDanhMuc, String noiDung) {
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thể loại không được để trống!");
        }
        String cleanName = tenDanhMuc.trim();
        if (danhMucRepository.findByTenDanhMucIgnoreCase(cleanName) != null) {
            throw new IllegalArgumentException("Thể loại '" + cleanName + "' đã tồn tại!");
        }
        DanhMuc dm = new DanhMuc();
        dm.setTenDanhMuc(cleanName);
        dm.setNoiDung(noiDung != null ? noiDung.trim() : "");
        dm.setTrangThai(true);
        dm.setNgayTao(LocalDate.now());
        dm.setNgaySua(LocalDate.now());
        return danhMucRepository.save(dm);
    }

    @Transactional
    public DanhMuc updateCategory(int id, String tenDanhMuc, String noiDung) {
        DanhMuc dm = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại với ID: " + id));

        if (tenDanhMuc != null && !tenDanhMuc.trim().isEmpty()) {
            String cleanName = tenDanhMuc.trim();
            DanhMuc existing = danhMucRepository.findByTenDanhMucIgnoreCase(cleanName);
            if (existing != null && existing.getId() != id) {
                throw new IllegalArgumentException("Thể loại '" + cleanName + "' đã được sử dụng!");
            }
            dm.setTenDanhMuc(cleanName);
        }

        if (noiDung != null) {
            dm.setNoiDung(noiDung.trim());
        }
        dm.setNgaySua(LocalDate.now());
        return danhMucRepository.save(dm);
    }

    @Transactional
    public DanhMuc toggleCategoryStatus(int id) {
        DanhMuc dm = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại với ID: " + id));
        dm.setTrangThai(!dm.isTrangThai());
        dm.setNgaySua(LocalDate.now());
        return danhMucRepository.save(dm);
    }

    @Transactional
    public void deleteCategory(int id) {
        if (!danhMucRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy thể loại với ID: " + id);
        }
        List<ComicDanhMuc> links = comicDanhMucRepository.findByDanhMucId(id, Pageable.unpaged()).getContent();
        if (links != null && !links.isEmpty()) {
            comicDanhMucRepository.deleteAll(links);
        }
        danhMucRepository.deleteById(id);
    }

    private UserAdminDTO toUserAdminDTO(User user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setTrangThai(user.isTrangThai());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleName(user.getRole().getRoleName());
        }
        dto.setNgayTao(user.getNgayTao());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

    private ComicAdminDTO toComicAdminDTO(Comic comic, String currentUsername, boolean isAdmin) {
        ComicAdminDTO dto = new ComicAdminDTO();
        dto.setId(comic.getId());
        dto.setTenTruyen(comic.getTenTruyen());
        dto.setImageComic(comic.getImageComic());
        dto.setLuotXem(comic.getLuotXem());
        dto.setLuotThich(comic.getLuotThich());
        dto.setTrangThai(comic.getTrangThai());
        dto.setNgayTao(comic.getNgayTao());
        dto.setNgaySua(comic.getNgaySua());

        String translator = (comic.getTranslator() != null) ? comic.getTranslator().getUsername() : "Chưa rõ";
        dto.setTranslatorUsername(translator);

        boolean canEdit = isAdmin || (comic.getTranslator() != null && comic.getTranslator().getUsername().equalsIgnoreCase(currentUsername));
        dto.setCanEdit(canEdit);

        // Danh sách tác giả
        if (comic.getComicTacGias() != null) {
            dto.setAuthors(comic.getComicTacGias().stream()
                    .filter(ctg -> ctg.getTacGia() != null)
                    .map(ctg -> ctg.getTacGia().getTenTacGia())
                    .collect(Collectors.toList()));
        } else {
            dto.setAuthors(Collections.emptyList());
        }

        // Danh sách thể loại
        if (comic.getComicDanhMucs() != null) {
            dto.setCategories(comic.getComicDanhMucs().stream()
                    .filter(cdm -> cdm.getDanhMuc() != null)
                    .map(cdm -> cdm.getDanhMuc().getTenDanhMuc())
                    .collect(Collectors.toList()));
        } else {
            dto.setCategories(Collections.emptyList());
        }

        // Số chapter
        dto.setChapterCount(comic.getChapters() != null ? comic.getChapters().size() : 0);

        return dto;
    }

    private ReportDTO toReportDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        if (report.getUsers() != null) {
            dto.setUserId(report.getUsers().getId());
            dto.setUsername(report.getUsers().getUsername());
        }
        if (report.getComics() != null) {
            dto.setComicId(report.getComics().getId());
            dto.setComicTitle(report.getComics().getTenTruyen());
        }
        dto.setLyDo(report.getLyDo());
        dto.setTrangThai(report.getTrangThai());
        dto.setNgayTao(report.getNgayTao());
        return dto;
    }
}
