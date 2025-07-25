package com.example.demo.baove.controller;

import com.example.demo.baove.controller.DTO.ComicDTO;
import com.example.demo.baove.entity.*;
import com.example.demo.baove.repository.*;
import com.example.demo.baove.security.JwtUtil;
import com.example.demo.baove.service.UserService;
import com.example.demo.baove.service.comicService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api/truyen")
public class comicController {

    private static final Logger logger = LoggerFactory.getLogger(comicController.class);

    @Autowired
    private comicService truyenService;

    @Autowired
    private comicRepository comicRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private wishlistRepository wishlistRepository;

    @Autowired
    private TacGiaRepository tacGiaRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ComicTacGiaRepository comicTacGiaRepository;

    @Autowired
    private ComicDanhMucRepository comicDanhMucRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cloudflare.r2.access-key}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key}")
    private String secretKey;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.upload-endpoint}")
    private String uploadEndpoint;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(uploadEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of("us-east-1"))
                .build();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createTruyen(
            HttpServletRequest request,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "note") String note,
            @RequestParam(value = "cover") MultipartFile cover,
            @RequestParam(value = "chapterZip", required = false) List<MultipartFile> chapterZips,
            @RequestParam(value = "chapterName", required = false) List<String> chapterNames,
            @RequestParam(value = "authorIds") List<Integer> authorIds,
            @RequestParam(value = "categoryIds") List<Integer> categoryIds) {

        logger.info("Bắt đầu tạo truyện mới với tiêu đề: {}", title);

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authorization header không hợp lệ hoặc thiếu Bearer");
            return ResponseEntity.status(401).body("Authorization header không hợp lệ hoặc thiếu Bearer");
        }

        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            logger.error("Token không hợp lệ: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Token không hợp lệ: " + e.getMessage());
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("Không tìm thấy người dùng với username: {}", username);
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }

        String role = jwtUtil.getRoleFromToken(token);
        if (!(role.equals("ROLE_ADMIN") || role.equals("ROLE_CHUTUT"))){
            logger.warn("Người dùng không có quyền tạo truyện mới: role={}", role);
            return ResponseEntity.status(403).body("Bạn không có quyền tạo truyện mới!");
        }

        // Kiểm tra xem truyện với tiêu đề này đã tồn tại chưa
        Optional<Comic> existingComic = comicRepository.findByTenTruyen(title);
        if (existingComic.isPresent()) {
            logger.warn("Truyện với tiêu đề '{}' đã tồn tại: ID {}", title, existingComic.get().getId());
            return ResponseEntity.status(400).body("Truyện với tiêu đề '" + title + "' đã tồn tại!");
        }

        Comic comic = new Comic();
        comic.setTenTruyen(title);
        comic.setMoTa(description);
        comic.setGhiChu(note);
        comic.setLuotXem(0);
        comic.setNgayTao(LocalDate.now());
        comic.setNgaySua(LocalDate.now());
        comic.setTranslator(user);

        String coverKey = "covers/" + UUID.randomUUID() + "-" + cover.getOriginalFilename();
        String coverUrl;
        try {
            coverUrl = uploadToR2(cover.getBytes(), coverKey);
        } catch (IOException e) {
            logger.error("Lỗi upload ảnh bìa lên R2: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Lỗi khi upload ảnh bìa: " + e.getMessage());
        }
        comic.setImageComic(coverUrl);

        entityManager.persist(comic);
        logger.info("Lưu truyện thành công: ID {}", comic.getId());

        if (authorIds != null && !authorIds.isEmpty()) {
            for (Integer authorId : authorIds) {
                TacGia tacGia = tacGiaRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Tác giả với ID " + authorId + " không tồn tại"));
                ComicTacGia comicTacGia = new ComicTacGia();
                comicTacGia.setTacGia(tacGia);
                comicTacGia.setComics(comic);
                entityManager.persist(comicTacGia);
                logger.info("Liên kết tác giả ID {} với truyện ID {}", authorId, comic.getId());
            }
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Integer categoryId : categoryIds) {
                DanhMuc danhMuc = danhMucRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Danh mục với ID " + categoryId + " không tồn tại"));
                ComicDanhMuc comicDanhMuc = new ComicDanhMuc();
                comicDanhMuc.setComics(comic);
                comicDanhMuc.setDanhMuc(danhMuc);
                entityManager.persist(comicDanhMuc);
                logger.info("Liên kết danh mục ID {} với truyện ID {}", categoryId, comic.getId());
            }
        }

        if (chapterZips != null && chapterNames != null && !chapterZips.isEmpty() && !chapterNames.isEmpty()) {
            if (chapterZips.size() != chapterNames.size()) {
                logger.warn("Danh sách chapter không hợp lệ: chapterZips.size={}, chapterNames.size={}",
                        chapterZips.size(), chapterNames.size());
                return ResponseEntity.status(400).body("Danh sách chapter không hợp lệ");
            }

            for (int i = 0; i < chapterZips.size(); i++) {
                MultipartFile zipFile = chapterZips.get(i);
                if (zipFile == null || zipFile.isEmpty()) {
                    logger.warn("Chapter ZIP tại index {} là rỗng", i);
                    continue;
                }

                Chapter chapter = new Chapter();
                chapter.setTenChap(chapterNames.get(i));
                chapter.setTrangThai(true);
                chapter.setNgayTao(LocalDate.now());
                chapter.setNgaySua(LocalDate.now());
                chapter.setIdComic(comic.getId());
                entityManager.persist(chapter);
                logger.info("Lưu Chapter thành công: ID {}", chapter.getId());

                try {
                    List<ImageChapter> chapterImages = processZipAndUpload(zipFile, chapter.getId());
                    for (ImageChapter image : chapterImages) {
                        entityManager.persist(image);
                        logger.info("Lưu ImageChapter thành công: ID {}", image.getId());
                    }
                } catch (IOException e) {
                    logger.error("Lỗi xử lý file ZIP chapter '{}': {}", chapterNames.get(i), e.getMessage(), e);
                    return ResponseEntity.status(500).body("Lỗi khi xử lý file ZIP chapter " + chapterNames.get(i) + ": " + e.getMessage());
                }
            }
        }

        logger.info("Tạo truyện mới thành công: ID {}", comic.getId());
        return ResponseEntity.ok("Tạo truyện thành công");
    }

    @PutMapping("/{id}/chapters")
    @Transactional
    public ResponseEntity<?> updateChapters(
            @PathVariable Integer id,
            HttpServletRequest request,
            @RequestParam(value = "chapterZip", required = false) List<MultipartFile> chapterZips,
            @RequestParam(value = "chapterName", required = false) List<String> chapterNames) {

        logger.info("Bắt đầu cập nhật chapter cho truyện ID: {}", id);

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authorization header không hợp lệ hoặc thiếu Bearer");
            return ResponseEntity.status(401).body("Authorization header không hợp lệ hoặc thiếu Bearer");
        }

        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            logger.error("Token không hợp lệ: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Token không hợp lệ: " + e.getMessage());
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("Không tìm thấy người dùng với username: {}", username);
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }

        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Truyện với ID " + id + " không tồn tại"));

        if (chapterZips == null || chapterNames == null || chapterZips.isEmpty() || chapterNames.isEmpty()) {
            logger.warn("Không có chapter được cung cấp: chapterZips={}, chapterNames={}", chapterZips, chapterNames);
            return ResponseEntity.status(400).body("Danh sách chapter trống");
        } else if (chapterZips.size() != chapterNames.size()) {
            logger.warn("Danh sách chapter không hợp lệ: chapterZips.size={}, chapterNames.size={}",
                    chapterZips.size(), chapterNames.size());
            return ResponseEntity.status(400).body("Danh sách chapter không hợp lệ");
        }

        for (int i = 0; i < chapterZips.size(); i++) {
            MultipartFile zipFile = chapterZips.get(i);
            String chapterName = chapterNames.get(i);
            if (zipFile == null || zipFile.isEmpty()) {
                logger.warn("Chapter ZIP tại index {} là rỗng", i);
                continue;
            }

            // Kiểm tra xem chapter với tên này đã tồn tại cho truyện này chưa
            Optional<Chapter> existingChapter = comic.getChapters().stream()
                    .filter(ch -> ch.getTenChap().equals(chapterName))
                    .findFirst();
            if (existingChapter.isPresent()) {
                logger.warn("Chapter với tên '{}' đã tồn tại cho truyện ID {}", chapterName, id);
                continue; // Bỏ qua nếu chapter đã tồn tại
            }

            Chapter chapter = new Chapter();
            chapter.setTenChap(chapterName);
            chapter.setTrangThai(true);
            chapter.setNgayTao(LocalDate.now());
            chapter.setNgaySua(LocalDate.now());
            chapter.setIdComic(comic.getId());
            entityManager.persist(chapter);
            logger.info("Lưu Chapter thành công: ID {}", chapter.getId());

            try {
                List<ImageChapter> chapterImages = processZipAndUpload(zipFile, chapter.getId());
                for (ImageChapter image : chapterImages) {
                    entityManager.persist(image);
                    logger.info("Lưu ImageChapter thành công: ID {}", image.getId());
                }
            } catch (IOException e) {
                logger.error("Lỗi xử lý file ZIP chapter '{}': {}", chapterNames.get(i), e.getMessage(), e);
                return ResponseEntity.status(500).body("Lỗi khi xử lý file ZIP chapter " + chapterNames.get(i) + ": " + e.getMessage());
            }
        }

        logger.info("Hoàn tất cập nhật chapter cho truyện ID: {}", id);
        return ResponseEntity.ok("Cập nhật chapter thành công");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> updateTruyen(
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody TruyenRequest request,
            @RequestParam(value = "authorIds", required = false) List<Integer> authorIds,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            HttpServletRequest httpRequest) {

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authorization header không hợp lệ hoặc thiếu Bearer");
            return ResponseEntity.status(401).body("Authorization header không hợp lệ hoặc thiếu Bearer");
        }

        String token = authHeader.substring(7);
        String username;
        String role;
        try {
            username = jwtUtil.getUsernameFromToken(token);
            role = jwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            logger.error("Token không hợp lệ: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Token không hợp lệ: " + e.getMessage());
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("Không tìm thấy người dùng với username: {}", username);
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }
        Integer userId = user.getId();

        try {
            Comic comic = comicRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Truyen voi ID " + id + " khong ton tai"));

            if (!(role.equals("ROLE_ADMIN") || role.equals("ROLE_CHUTUT"))){
                logger.warn("Nguoi dung khong co quyen cap nhat truyen: role={}", role);
                return ResponseEntity.status(403).body("Ban khong co quyen cap nhat truyen!");
            }

            if (request.getMoTa() != null) {
                comic.setMoTa(request.getMoTa());
            }
            if (request.getGhiChu() != null) {
                comic.setGhiChu(request.getGhiChu());
            }
            comic.setNgaySua(LocalDate.now());

            // Cập nhật tác giả
            if (authorIds != null && !authorIds.isEmpty()) {
                List<ComicTacGia> existingTacGias = comicTacGiaRepository.findByComicsId(id);
                for (ComicTacGia comicTacGia : existingTacGias) {
                    entityManager.remove(comicTacGia);
                }
                for (Integer authorId : authorIds) {
                    TacGia tacGia = tacGiaRepository.findById(authorId)
                            .orElseThrow(() -> new RuntimeException("Tac gia voi ID " + authorId + " khong ton tai"));
                    ComicTacGia comicTacGia = new ComicTacGia();
                    comicTacGia.setTacGia(tacGia);
                    comicTacGia.setComics(comic);
                    entityManager.persist(comicTacGia);
                    logger.info("Cap nhat tac gia ID {} cho truyen ID {}", authorId, comic.getId());
                }
            }

            // Cập nhật danh mục (xóa cũ, thêm mới)
            if (categoryIds != null && !categoryIds.isEmpty()) {
                List<ComicDanhMuc> existingDanhMucs = comicDanhMucRepository.findByComicsId(id);
                for (ComicDanhMuc comicDanhMuc : existingDanhMucs) {
                    entityManager.remove(comicDanhMuc);
                }
                for (Integer categoryId : categoryIds) {
                    DanhMuc danhMuc = danhMucRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Danh muc voi ID " + categoryId + " khong ton tai"));
                    ComicDanhMuc comicDanhMuc = new ComicDanhMuc();
                    comicDanhMuc.setDanhMuc(danhMuc);
                    comicDanhMuc.setComics(comic);
                    entityManager.persist(comicDanhMuc);
                    logger.info("Cap nhat danh muc ID {} cho truyen ID {}", categoryId, comic.getId());
                }
            }

            entityManager.merge(comic);
            logger.info("Cap nhat truyen ID {} thanh cong", id);
            return ResponseEntity.ok(comic);
        } catch (RuntimeException e) {
            logger.error("Loi khi cap nhat truyen ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(400).body("Loi khi cap nhat truyen: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Loi khong xac dinh khi cap nhat truyen ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Loi khong xac dinh khi cap nhat truyen: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ComicDTO>> getAllTruyen() {
        try {
            List<Comic> truyenList = comicRepository.findAll();
            List<ComicDTO> comicDTOs = truyenList.stream().map(comic -> {
                ComicDTO dto = new ComicDTO();
                dto.setId(comic.getId());
                dto.setTenTruyen(comic.getTenTruyen());
                dto.setMoTa(comic.getMoTa());
                dto.setGhiChu(comic.getGhiChu());
                dto.setImageComic(comic.getImageComic());
                dto.setLuotXem(comic.getLuotXem());
                dto.setLuotThich(comic.getLuotThich());
                dto.setNgayTao(comic.getNgayTao());
                dto.setNgaySua(comic.getNgaySua());
                // Lấy danh sách categoryIds
                List<Integer> categoryIds = comic.getComicDanhMucs().stream()
                        .map(comicDanhMuc -> comicDanhMuc.getDanhMuc().getId())
                        .collect(Collectors.toList());
                dto.setCategoryIds(categoryIds);
                // Lấy danh sách authorIds
                List<Integer> authorIds = comic.getComicTacGias().stream()
                        .map(comicTacGia -> comicTacGia.getTacGia().getId())
                        .collect(Collectors.toList());
                dto.setAuthorIds(authorIds);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(comicDTOs);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/listloc")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ComicDTO>> getAllTruyendaloc(Principal principal) {
        try {
            List<Comic> truyenList = comicRepository.findAll();
            List<ComicDTO> comicDTOs = truyenList.stream().filter(comic -> comic.getTranslator().getUsername().equals(principal.getName())).map(comic -> {
                ComicDTO dto = new ComicDTO();
                dto.setId(comic.getId());
                dto.setTenTruyen(comic.getTenTruyen());
                dto.setMoTa(comic.getMoTa());
                dto.setGhiChu(comic.getGhiChu());
                dto.setImageComic(comic.getImageComic());
                dto.setLuotXem(comic.getLuotXem());
                dto.setLuotThich(comic.getLuotThich());
                dto.setNgayTao(comic.getNgayTao());
                dto.setNgaySua(comic.getNgaySua());
                // Lấy danh sách categoryIds
                List<Integer> categoryIds = comic.getComicDanhMucs().stream()
                        .map(comicDanhMuc -> comicDanhMuc.getDanhMuc().getId())
                        .collect(Collectors.toList());
                dto.setCategoryIds(categoryIds);
                // Lấy danh sách authorIds
                List<Integer> authorIds = comic.getComicTacGias().stream()
                        .map(comicTacGia -> comicTacGia.getTacGia().getId())
                        .collect(Collectors.toList());
                dto.setAuthorIds(authorIds);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(comicDTOs);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/authors")
    @Transactional(readOnly = true)
   // @PreAuthorize("hasAnyRole('ADMIN', 'CHUTUT')")
    public ResponseEntity<List<TacGia>> getAllAuthors(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        logger.info("Received request with authHeader: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authorization header không hợp lệ hoặc thiếu Bearer");
            return ResponseEntity.status(401).body(null);
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            logger.info("Extracted username: {}", username);
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body(null);
            }

            String role = jwtUtil.getRoleFromToken(token);
            logger.info("User role: {}", role);
            if (!(role.equals("ROLE_ADMIN") || role.equals("ROLE_CHUTUT"))) {
                logger.warn("Người dùng không có quyền truy cập danh sách tác giả: role={}", role);
                return ResponseEntity.status(403).body(null);
            }

            List<TacGia> authors = tacGiaRepository.findAll();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách tác giả: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/authors")
    public ResponseEntity<TacGia> addAuthorNew(@org.springframework.web.bind.annotation.RequestBody TacGia author, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authorization header không hợp lệ hoặc thiếu Bearer");
            return ResponseEntity.status(401).body(null);
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body(null);
            }

            String role = jwtUtil.getRoleFromToken(token);
            if (!(role.equals("ROLE_ADMIN") || role.equals("ROLE_CHUTUT"))) {
                logger.warn("Người dùng không có quyền thêm tác giả: role={}", role);
                return ResponseEntity.status(403).body(null);
            }

            author.setNgayTao(LocalDate.now());
            author.setTrangThai(true);
            TacGia savedAuthor = tacGiaRepository.save(author);
            return ResponseEntity.ok(savedAuthor);
        } catch (Exception e) {
            logger.error("Lỗi khi thêm tác giả: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/hot")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ComicDTO>> getHotTruyen() {
        try {
            List<Comic> hotTruyen = comicRepository.findTop6ByOrderByLuotXemDesc();
            List<ComicDTO> comicDTOs = hotTruyen.stream().map(comic -> {
                ComicDTO dto = new ComicDTO();
                dto.setId(comic.getId());
                dto.setTenTruyen(comic.getTenTruyen());
                dto.setMoTa(comic.getMoTa());
                dto.setGhiChu(comic.getGhiChu());
                dto.setImageComic(comic.getImageComic());
                dto.setLuotXem(comic.getLuotXem());
                dto.setNgayTao(comic.getNgayTao());
                dto.setNgaySua(comic.getNgaySua());
                // Lấy danh sách categoryIds
                List<Integer> categoryIds = comic.getComicDanhMucs().stream()
                        .map(comicDanhMuc -> comicDanhMuc.getDanhMuc().getId())
                        .collect(Collectors.toList());
                dto.setCategoryIds(categoryIds);
                // Lấy danh sách authorIds
                List<Integer> authorIds = comic.getComicTacGias().stream()
                        .map(comicTacGia -> comicTacGia.getTacGia().getId())
                        .collect(Collectors.toList());
                dto.setAuthorIds(authorIds);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(comicDTOs);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện hot: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/moi")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ComicDTO>> getMoiTruyen() {
        try {
            List<Comic> moiTruyen = comicRepository.findTop5ByOrderByNgayTaoDesc();
            List<ComicDTO> comicDTOs = moiTruyen.stream().map(comic -> {
                ComicDTO dto = new ComicDTO();
                dto.setId(comic.getId());
                dto.setTenTruyen(comic.getTenTruyen());
                dto.setMoTa(comic.getMoTa());
                dto.setGhiChu(comic.getGhiChu());
                dto.setImageComic(comic.getImageComic());
                dto.setLuotXem(comic.getLuotXem());
                dto.setNgayTao(comic.getNgayTao());
                dto.setNgaySua(comic.getNgaySua());

                List<Integer> categoryIds = comic.getComicDanhMucs().stream()
                        .map(comicDanhMuc -> comicDanhMuc.getDanhMuc().getId())
                        .collect(Collectors.toList());
                dto.setCategoryIds(categoryIds);

                List<Integer> authorIds = comic.getComicTacGias().stream()
                        .map(comicTacGia -> comicTacGia.getTacGia().getId())
                        .collect(Collectors.toList());
                dto.setAuthorIds(authorIds);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(comicDTOs);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện mới: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/favorite/list")
    public ResponseEntity<List<FavoriteResponse>> getFavoriteList(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body(null);
            }

            List<Wishlist> favorites = wishlistRepository.findByUsers(user);
            List<FavoriteResponse> response = favorites.stream().map(favorite -> {
                FavoriteResponse fr = new FavoriteResponse();
                fr.setComicId((long) favorite.getComics().getId());
                fr.setTenTruyen(favorite.getComics().getTenTruyen());
                fr.setImageComic(favorite.getComics().getImageComic());
                return fr;
            }).collect(Collectors.toList());

            logger.info("Lấy danh sách yêu thích thành công cho user: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách yêu thích: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/favorite/status/{truyenId}")
    public ResponseEntity<Boolean> checkFavoriteStatus(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body(false);
            }

            Comic truyen = comicRepository.findById(truyenId)
                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));
            boolean isFavorited = wishlistRepository.existsByUsersAndComics(user, truyen);
            return ResponseEntity.ok(isFavorited);
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra trạng thái yêu thích cho truyện ID {}: {}", truyenId, e.getMessage(), e);
            return ResponseEntity.status(401).body(false);
        }
    }

    @PostMapping("/favorite/{truyenId}")
    @Transactional
    public ResponseEntity<?> addToFavorite(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt);

            if (!role.equals("ROLE_CHUTUT") && !role.equals("ROLE_DOCGIA") && !role.equals("ROLE_ADMIN")) {
                logger.warn("Người dùng không có quyền thêm truyện vào danh sách yêu thích: role={}", role);
                return ResponseEntity.status(403).body("Bạn không có quyền thêm truyện vào danh sách yêu thích!");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body("Người dùng không tồn tại!");
            }

            Comic truyen = comicRepository.findById(truyenId)
                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));

            if (wishlistRepository.existsByUsersAndComics(user, truyen)) {
                logger.info("Truyện ID {} đã có trong danh sách yêu thích của người dùng {}", truyenId, username);
                return ResponseEntity.status(200).body("Truyện đã có trong danh sách yêu thích!");
            }

            Wishlist favorite = new Wishlist();
            favorite.setUsers(user);
            favorite.setComics(truyen);
            favorite.setNgayTao(LocalDate.now());
            wishlistRepository.save(favorite);

            // Tăng luotThich sau khi thêm thành công
            truyen.setLuotThich(truyen.getLuotThich() + 1);
            comicRepository.save(truyen);

            logger.info("Thêm truyện ID {} vào danh sách yêu thích của người dùng {} thành công", truyenId, username);
            return ResponseEntity.ok("Thêm vào yêu thích thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi thêm truyện ID {} vào danh sách yêu thích: {}", truyenId, e.getMessage(), e);
            return ResponseEntity.status(401).body("Token không hợp lệ hoặc lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/favorite/{truyenId}")
    @Transactional
    public ResponseEntity<?> removeFromFavorite(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt);

            if (!role.equals("ROLE_CHUTUT") && !role.equals("ROLE_DOCGIA") && !role.equals("ROLE_ADMIN")) {
                logger.warn("Người dùng không có quyền thêm truyện vào danh sách yêu thích: role={}", role);
                return ResponseEntity.status(403).body("Bạn không có quyền thêm truyện vào danh sách yêu thích!");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body("Người dùng không tồn tại!");
            }

            Comic truyen = comicRepository.findById(truyenId)
                    .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));

            Wishlist favorite = wishlistRepository.findByUsersAndComics(user, truyen)
                    .orElseThrow(() -> new RuntimeException("Truyện không có trong danh sách yêu thích!"));

            // Giảm luotThich trước khi xóa
            truyen.setLuotThich(truyen.getLuotThich() > 0 ? truyen.getLuotThich() - 1 : 0);
            comicRepository.save(truyen);

            wishlistRepository.delete(favorite);

            logger.info("Xóa truyện ID {} khỏi danh sách yêu thích của người dùng {} thành công", truyenId, username);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích!");
        } catch (Exception e) {
            logger.error("Lỗi khi xóa truyện ID {} khỏi danh sách yêu thích: {}", truyenId, e.getMessage(), e);
            return ResponseEntity.status(401).body("Token không hợp lệ hoặc lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ComicDTO>> searchComics(@RequestParam("query") String query) {
        try {
            List<Comic> comics = comicRepository.findByTenTruyenContainingIgnoreCase(query);
            List<ComicDTO> comicDTOs = comics.stream().map(comic -> {
                ComicDTO dto = new ComicDTO();
                dto.setId(comic.getId());
                dto.setTenTruyen(comic.getTenTruyen());
                dto.setMoTa(comic.getMoTa());
                dto.setGhiChu(comic.getGhiChu());
                dto.setImageComic(comic.getImageComic());
                dto.setLuotXem(comic.getLuotXem());
                dto.setNgayTao(comic.getNgayTao());
                dto.setNgaySua(comic.getNgaySua());

                List<Integer> categoryIds = comic.getComicDanhMucs().stream()
                        .map(comicDanhMuc -> comicDanhMuc.getDanhMuc().getId())
                        .collect(Collectors.toList());
                dto.setCategoryIds(categoryIds);

                List<Integer> authorIds = comic.getComicTacGias().stream()
                        .map(comicTacGia -> comicTacGia.getTacGia().getId())
                        .collect(Collectors.toList());
                dto.setAuthorIds(authorIds);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(comicDTOs);
        } catch (Exception e) {
            logger.error("Lỗi khi tìm kiếm truyện với query '{}': {}", query, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<ComicDTO> getComicById(@PathVariable("id") int id) {
        try {
            Comic comic = comicRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Truyện với ID " + id + " không tồn tại"));


            comic.setLuotXem(comic.getLuotXem() + 1);
            comicRepository.save(comic);


            ComicDTO dto = new ComicDTO();
            dto.setId(comic.getId());
            dto.setTenTruyen(comic.getTenTruyen());
            dto.setMoTa(comic.getMoTa());
            dto.setGhiChu(comic.getGhiChu());
            dto.setImageComic(comic.getImageComic());
            dto.setLuotXem(comic.getLuotXem());
            dto.setNgayTao(comic.getNgayTao());
            dto.setLuotThich(comic.getLuotThich());
            dto.setNgaySua(comic.getNgaySua());
            dto.setUser(comic.getTranslator());
            List<Integer> categoryIds = comic.getComicDanhMucs().stream()
                    .map(comicDanhMuc -> comicDanhMuc.getDanhMuc().getId())
                    .collect(Collectors.toList());
            dto.setCategoryIds(categoryIds);
            // Lấy danh sách authorIds
            List<Integer> authorIds = comic.getComicTacGias().stream()
                    .map(comicTacGia -> comicTacGia.getTacGia().getId())
                    .collect(Collectors.toList());
            dto.setAuthorIds(authorIds);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy truyện với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body(null);
        }
    }

    private String uploadToR2(byte[] data, String key) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            String contentType;
            String fileExtension = key.substring(key.lastIndexOf('.') + 1).toLowerCase();
            if ("jpg".equals(fileExtension) || "jpeg".equals(fileExtension)) {
                contentType = "image/jpeg";
            } else if ("png".equals(fileExtension)) {
                contentType = "image/png";
            } else {
                contentType = "application/octet-stream";
            }
            logger.info("Upload file với Content-Type: {}", contentType);

            long contentLength = data.length;
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentDisposition("inline")
                    .acl("public-read")
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
            String fullUrl = publicUrl + "/" + key;
            logger.info("File uploaded to R2: {}", fullUrl);
            return fullUrl;
        } catch (Exception e) {
            logger.error("Lỗi upload lên R2: key={}, error={}", key, e.getMessage(), e);
            throw new IOException("Không thể upload file lên R2: " + e.getMessage(), e);
        }
    }

    private void verifyObjectExists(String key) throws IOException {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headRequest);
            logger.info("Xác minh thành công: File tồn tại trên R2, key={}", key);
        } catch (NoSuchKeyException e) {
            logger.error("File không tồn tại trên R2: key={}", key);
            throw new IOException("File không tồn tại trên R2: " + key);
        } catch (Exception e) {
            logger.error("Lỗi xác minh file trên R2: key={}, error={}", key, e.getMessage(), e);
            throw new IOException("Lỗi xác minh file trên R2: " + e.getMessage(), e);
        }
    }

    private List<ImageChapter> processZipAndUpload(MultipartFile zipFile, Integer chapterId) throws IOException {
        List<ImageChapter> images = new ArrayList<>();
        Map<String, byte[]> fileDataMap = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().matches("^\\d+\\.(jpg|png|jpeg)$")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    fileDataMap.put(entry.getName(), baos.toByteArray());
                }
                zis.closeEntry();
            }
        }

        List<String> fileNames = new ArrayList<>(fileDataMap.keySet());
        fileNames.sort((a, b) -> {
            int numA = Integer.parseInt(a.replaceAll("[^0-9]", ""));
            int numB = Integer.parseInt(b.replaceAll("[^0-9]", ""));
            return Integer.compare(numA, numB);
        });

        logger.info("Danh sách file sau khi sắp xếp: {}", fileNames);

        int pageNumber = 1;
        Chapter chapter = entityManager.find(Chapter.class, chapterId);
        if (chapter == null) {
            logger.error("Chapter with ID {} not found", chapterId);
            return images;
        }

        for (String fileName : fileNames) {
            logger.info("Processing ZIP entry: {}", fileName);

            byte[] imageData = fileDataMap.get(fileName);

            String imageKey = "chapters/" + chapterId + "/" + UUID.randomUUID() + "-" + fileName;
            String imageUrl = uploadToR2(imageData, imageKey);

            ImageChapter image = new ImageChapter();
            image.setChapter(chapter);
            image.setImageUrl(imageUrl);
            image.setPageNumber(pageNumber++);
            image.setNgayTao(LocalDate.now());
            images.add(image);
            logger.info("Extracted image: {} at page {}", imageUrl, pageNumber - 1);
        }

        return images;
    }
}

class TruyenRequest {
    private String moTa;
    private String ghiChu;

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }


}
class FavoriteResponse {
    private Long comicId;
    private String tenTruyen;
    private String imageComic;

    public Long getComicId() {
        return comicId;
    }

    public void setComicId(Long comicId) {
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
}