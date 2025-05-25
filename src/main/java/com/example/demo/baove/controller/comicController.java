package com.example.demo.baove.controller;

import com.example.demo.baove.entity.Chapter;
import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.entity.ImageChapter;
import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Wishlist;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.repository.wishlistRepository;
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
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDate;
import java.util.*;
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
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

//    @Value("${cloudflare.r2.access-key}")
//    private String accessKey;
//
//    @Value("${cloudflare.r2.secret-key}")
//    private String secretKey;
//
//    @Value("${cloudflare.r2.bucket-name}")
//    private String bucketName;
@Autowired
private String accessKey;

    @Autowired
    private String secretKey;

    @Autowired
    private String bucketName;


    @Autowired
    private String uploadEndpoint;

    @Autowired
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
            @RequestParam(value = "chapterName", required = false) List<String> chapterNames) {

        logger.info("Bắt đầu tạo truyện mới với tiêu đề: {}", title);

        // Xác thực token
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

        // Kiểm tra quyền admin
        String role = jwtUtil.getRoleFromToken(token);
        if (!role.equals("ROLE_admin")) {
            logger.warn("Người dùng không có quyền tạo truyện mới: role={}", role);
            return ResponseEntity.status(403).body("Bạn không có quyền tạo truyện mới!");
        }

        // Tạo truyện mới
        Comic comic = new Comic();
        comic.setTenTruyen(title);
        comic.setMoTa(description);
        comic.setGhiChu(note);
        comic.setLuotXem(0); // Giá trị mặc định
        comic.setNgayTao(LocalDate.now());
        comic.setNgaySua(LocalDate.now());
        comic.setTranslator(user); // Gán user làm translator thay vì idUser

        // Upload ảnh bìa lên R2
        String coverKey = "covers/" + UUID.randomUUID() + "-" + cover.getOriginalFilename();
        String coverUrl;
        try {
            coverUrl = uploadToR2(cover.getBytes(), coverKey);
        } catch (IOException e) {
            logger.error("Lỗi upload ảnh bìa lên R2: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Lỗi khi upload ảnh bìa: " + e.getMessage());
        }
        comic.setImageComic(coverUrl); // Sử dụng imageComic thay vì anhBia

        // Lưu truyện vào database
        entityManager.persist(comic);
        logger.info("Lưu truyện thành công: ID {}", comic.getId());

        // Xử lý các chapter (nếu có)
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

        // Xác thực token
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
        } else if (chapterZips.size() != chapterNames.size()) {
            logger.warn("Danh sách chapter không hợp lệ: chapterZips.size={}, chapterNames.size={}",
                    chapterZips.size(), chapterNames.size());
        } else {
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

        logger.info("Hoàn tất cập nhật chapter cho truyện ID: {}", id);
        return ResponseEntity.ok("Cập nhật chapter thành công");
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

        // Bước 1: Đọc và lưu dữ liệu file từ ZIP vào bộ nhớ
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

        // Log danh sách file sau khi sắp xếp để kiểm tra
        logger.info("Danh sách file sau khi sắp xếp: {}", fileNames);

        // Bước 3: Xử lý file theo thứ tự đã sắp xếp
        int pageNumber = 1;
        Chapter chapter = entityManager.find(Chapter.class, chapterId);
        if (chapter == null) {
            logger.error("Chapter with ID {} not found", chapterId);
            return images;
        }

        for (String fileName : fileNames) {
            logger.info("Processing ZIP entry: {}", fileName);

            // Lấy dữ liệu file từ Map
            byte[] imageData = fileDataMap.get(fileName);

            // Upload lên R2
            String imageKey = "chapters/" + chapterId + "/" + UUID.randomUUID() + "-" + fileName;
            String imageUrl = uploadToR2(imageData, imageKey);

            // Tạo ImageChapter
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTruyen(@PathVariable Integer id, @org.springframework.web.bind.annotation.RequestBody TruyenRequest request) {
        String token = SecurityContextHolder.getContext().getAuthentication().getDetails().toString();
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Integer userId = userService.findByUsername(username).getId();

        try {
            Comic truyen = truyenService.updateTruyen(id, request.getMoTa(), request.getGhiChu(), userId, role);
            return ResponseEntity.ok(truyen);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật truyện ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Lỗi khi cập nhật truyện: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Comic>> getAllTruyen() {
        try {
            List<Comic> truyenList = comicRepository.findAll();
            return ResponseEntity.ok(truyenList);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/hot")
    public ResponseEntity<List<Comic>> getHotTruyen() {
        try {
            List<Comic> hotTruyen = comicRepository.findTop5ByOrderByLuotXemDesc();
            return ResponseEntity.ok(hotTruyen);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện hot: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/moi")
    public ResponseEntity<List<Comic>> getMoiTruyen() {
        try {
            List<Comic> moiTruyen = comicRepository.findTop5ByOrderByNgayTaoDesc();
            return ResponseEntity.ok(moiTruyen);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách truyện mới: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
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
    public ResponseEntity<?> addToFavorite(@PathVariable int truyenId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt);

            if (!role.equals("ROLE_user") && !role.equals("ROLE_translator") && !role.equals("ROLE_admin")) {
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
            logger.info("Thêm truyện ID {} vào danh sách yêu thích của người dùng {} thành công", truyenId, username);

            return ResponseEntity.ok("Thêm vào yêu thích thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi thêm truyện ID {} vào danh sách yêu thích: {}", truyenId, e.getMessage(), e);
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
                logger.warn("Người dùng không có quyền xóa truyện khỏi danh sách yêu thích: role={}", role);
                return ResponseEntity.status(403).body("Bạn không có quyền xóa truyện khỏi danh sách yêu thích!");
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

            wishlistRepository.delete(favorite);
            logger.info("Xóa truyện ID {} khỏi danh sách yêu thích của người dùng {} thành công", truyenId, username);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích!");
        } catch (Exception e) {
            logger.error("Lỗi khi xóa truyện ID {} khỏi danh sách yêu thích: {}", truyenId, e.getMessage(), e);
            return ResponseEntity.status(401).body("Token không hợp lệ hoặc lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Comic>> searchComics(@RequestParam("query") String query) {
        try {
            List<Comic> comics = comicRepository.findByTenTruyenContainingIgnoreCase(query);
            return ResponseEntity.ok(comics);
        } catch (Exception e) {
            logger.error("Lỗi khi tìm kiếm truyện với query '{}': {}", query, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comic> getComicById(@PathVariable("id") int id) {
        try {
            Comic comic = comicRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Truyện với ID " + id + " không tồn tại"));
            return ResponseEntity.ok(comic);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy truyện với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body(null);
        }
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