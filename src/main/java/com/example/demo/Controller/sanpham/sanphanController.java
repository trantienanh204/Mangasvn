
package com.example.demo.Controller.sanpham;

import com.example.demo.entity.*;
import com.example.demo.respository.DanhMucRepository;
import com.example.demo.service.QuenmatkhauService;
import com.example.demo.service.hinhanhService;
import com.example.demo.service.SanPhamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("panda")
public class sanphanController {

    private static final Logger logger = LoggerFactory.getLogger(sanphanController.class);
    @Autowired
    private hinhanhService hinhanhService;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private QuenmatkhauService quenmatkhauService;
    @Autowired
    private DanhMucRepository danhMucRepository;
    @GetMapping("/sanpham/list")
    public Page<SanPham> getSanPhamList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "tensp", required = false) String tensp,
            @RequestParam(value = "trangthai", required = false) Integer trangthai) {

        Page<SanPham> sanPhamPage = sanPhamService.hienThiSanPhamTheoDieuKien(page, tensp, trangthai);

        // Kiểm tra nếu sanPhamPage là null
        if (sanPhamPage == null) {
            // Tạo một Pageable mới với page và size giống như bạn đã định nghĩa
            Pageable pageable = PageRequest.of(page, 5);
            return new PageImpl<>(Collections.emptyList(), pageable, 0); // Trả về một trang rỗng
        }
        return sanPhamPage;
    }

    @GetMapping("/Listsanpham")
    public List<Map<String, Object>> Listsanpham() {
        return sanPhamService.getallsp().stream().map(sp -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sp.getId());
            map.put("text", sp.getTensp());
            return map;
        }).collect(Collectors.toList());
    }


    //dat biet
    @GetMapping("/Listnxs")
    public List<Map<String, Object>> ListNXS() {
        return sanPhamService.getallNXS().stream()
                .filter(nxs -> nxs.getTrangthai()==1)
                .map(nxs -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", nxs.getId());
            map.put("text", nxs.getTennsx());
            return map;
        }).collect(Collectors.toList());
    }

    //dac biet
    @GetMapping("/Listdanhmuc")
    public List<Map<String, Object>> ListDanhmuc() {
        return sanPhamService.getallDanhmuc().stream().filter(danhMuc -> danhMuc.getTrangthai()==1).map(danhmuc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", danhmuc.getId());
            map.put("text", danhmuc.getTendanhmuc());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/Listchatlieu")
    public List<Map<String, Object>> ListChatLieu() {
        return sanPhamService.getallCL().stream().filter(chatlieu -> chatlieu.getTrangThai()==0).map(chatlieu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", chatlieu.getId());
            map.put("text", chatlieu.getTenChatLieu());
            return map;
        }).collect(Collectors.toList());
    }

    //dac biet
    @GetMapping("/Listthuonghieu")
    public List<Map<String, Object>> ListThuongHieu() {
        return sanPhamService.getallthuonghieu().stream().filter(thuonghieu -> thuonghieu.getTrangthai()==1).map(thuonghieu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", thuonghieu.getId());
            map.put("text", thuonghieu.getTenthuonghieu());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/Listcoao")
    public List<Map<String, Object>> ListCoAo() {
        return sanPhamService.getallcoao().stream().filter(coAo -> coAo.getTrangThai()==0).map(coao -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", coao.getId());
            map.put("text", coao.getTen());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/Listkichthuoc")
    public List<Map<String, Object>> Listkichthuoc() {
        return sanPhamService.getallkichco().stream().filter(kichThuoc -> kichThuoc.getTrangthai()==0).map(coao -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", coao.getId());
            map.put("text", coao.getTen());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/Listmausac")
    public List<Map<String, Object>> Listmausac() {
        return sanPhamService.getallmausac().stream().filter(mauSac -> mauSac.getTrangthai()==0).map(mausac -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", mausac.getId());
            map.put("text", mausac.getTen());
            map.put("colorCode", mausac.getMa());
            return map;
        }).collect(Collectors.toList());
    }


//    @PostMapping("/themTatCa")
//    public ResponseEntity<?> themTatCa(@RequestBody ProductDTO productDTO) {
//
//        Product newProduct = productService.addProduct(productDTO);
//
//        return ResponseEntity.ok(newProduct);
//    }
//@PostMapping("/taoSanPhamChiTiet")
//public ResponseEntity<?> taoSanPhamChiTiet(@RequestBody sanPhamDTO productDTO) {
//    if (productDTO == null) {
//        return ResponseEntity.badRequest().body("Product data is required.");
//    }
//
//    try {
//        SanPham newProduct = sanPhamService.convertToEntity(productDTO);
//
//        sanPhamService.createTemporarySanPhamChiTiet(newProduct, productDTO.getSizes(), productDTO.getColors());
//
//        return ResponseEntity.ok(sanPhamService.getTemporarySanPhamChiTietList());
//    } catch (EntityNotFoundException e) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found: " + e.getMessage());
//    } catch (IllegalArgumentException e) {
//        // Xử lý ngoại lệ đối với dữ liệu không hợp lệ
//        return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
//    } catch (Exception e) {
//        // Xử lý ngoại lệ chung
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product details: " + e.getMessage());
//    }
//}

    @PostMapping("/themTatCaSanPhamChiTiet")
    public ResponseEntity<?> themTatCaSanPhamChiTiet() {
        try {
            int savedCount = sanPhamService.saveTemporarySanPhamChiTiet();
            return ResponseEntity.ok(savedCount + " product details saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product details: " + e.getMessage());
        }
    }


    @GetMapping("/danhSachSanPhamChiTiet")
    public ResponseEntity<?> getDanhSachSanPhamChiTiet() {
        try {
            // Thay đổi kiểu trả về cho nhất quán
            List<SanPhamChiTiet> productDetails = sanPhamService.getTemporarySanPhamChiTietList();
            return ResponseEntity.ok(productDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving product details: " + e.getMessage());
        }
    }

    @PutMapping("/capNhatSanPhamChiTiet/{id}")
    public ResponseEntity<?> capNhatSanPhamChiTiet(@PathVariable Long id, @RequestBody SanPhamChiTietDTO updatedProductDetail) {
        if (updatedProductDetail == null) {
            return ResponseEntity.badRequest().body("Product detail data is required");
        }

        try {
            // Gọi service để cập nhật thông tin sản phẩm chi tiết
            boolean updated = sanPhamService.updateTemporarySanPhamChiTiet(id, updatedProductDetail);
            if (updated) {
                return ResponseEntity.ok("Product detail updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product detail not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product detail: " + e.getMessage());
        }
    }


    @DeleteMapping("/xoaSanPhamChiTiet/{id}")
    public ResponseEntity<?> xoaSanPhamChiTiet(@PathVariable Long id) {
        try {
            boolean deleted = sanPhamService.deleteTemporarySanPhamChiTiet(id);
            if (deleted) {
                return ResponseEntity.ok("Product detail deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product detail not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product detail: " + e.getMessage());
        }
    }

    @GetMapping("sanpham/hinh-anh/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        Optional<SanPham> sanPhamOptional = Optional.ofNullable(sanPhamService.Listtimkiemsp(id));
        if (sanPhamOptional.isPresent()) {
            byte[] image = sanPhamOptional.get().getAnhsp();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);  // Đảm bảo ảnh là JPEG hoặc có thể là PNG
            headers.setContentLength(image.length);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("xoa/{id}")
        public String xoa(@PathVariable Integer id) {
            if(id != null ){
                hinhanhService.xoa(id);
            }
            return "redirect:/baove/form";
        }

    @PostMapping("/addSP")
    public ResponseEntity<SanPham> addProduct(@RequestBody @Valid laytamDTO laytamDTO) {
        if (laytamDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        SanPham sanPham = new SanPham();
        sanPham.setTensp(laytamDTO.getName());
        sanPham.setTrangthai(1);
        sanPham.setNgaytao(LocalDate.now());
        sanPham.setMasp(quenmatkhauService.random());

        try {
            sanPhamService.addsanpham(sanPham);
            logger.info("Successfully added product: {}", sanPham);
            return ResponseEntity.status(HttpStatus.CREATED).body(sanPham);
        } catch (Exception e) {
            logger.error("Error adding product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/addChatLieu")
    public ResponseEntity<ChatLieu> addCL(@RequestBody @Valid laytamDTO laytamDTO) {
        if (laytamDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ChatLieu CL = new ChatLieu();
        CL.setTenChatLieu(laytamDTO.getName());
        CL.setTrangThai(0);
        CL.setNgayTao(LocalDateTime.now());
        CL.setMaChatLieu(quenmatkhauService.random());

        try {
            sanPhamService.addCL(CL);
            logger.info("Successfully added product: {}", CL);
            return ResponseEntity.status(HttpStatus.CREATED).body(CL);
        } catch (Exception e) {
            logger.error("Error adding product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/addCoAo")
    public ResponseEntity<CoAo> addCollar(@RequestBody laytamDTO laytamDTO) {
        if (laytamDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        CoAo coAo = new CoAo();
        coAo.setTen(laytamDTO.getName());
        coAo.setTrangThai(0);
        coAo.setNgayTao(LocalDateTime.now());
        coAo.setMa(quenmatkhauService.random());
        try {
            sanPhamService.addcoao(coAo);
            return ResponseEntity.status(HttpStatus.CREATED).body(coAo);
        } catch (Exception e) {
            logger.error("Error adding collar: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/addNSX")
    public ResponseEntity<NhaSanXuat> addManufacturer(@RequestBody laytamDTO laytamDTO) {
        if (laytamDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        NhaSanXuat nhaSanXuat = new NhaSanXuat();
        nhaSanXuat.setTennsx(laytamDTO.getName());
        nhaSanXuat.setTrangthai(1);
        nhaSanXuat.setNgaytao(LocalDate.now());
        nhaSanXuat.setMansx(quenmatkhauService.random());
        try {
            sanPhamService.addNXS(nhaSanXuat);
            return ResponseEntity.status(HttpStatus.CREATED).body(nhaSanXuat);
        } catch (Exception e) {
            logger.error("Error adding manufacturer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/addDanhMuc")
    public ResponseEntity<DanhMuc> addCategory(@RequestBody laytamDTO laytamDTO) {
        if (laytamDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setTendanhmuc(laytamDTO.getName());
        danhMuc.setNgaysua(LocalDate.now());
        danhMuc.setTrangthai(1);
        danhMuc.setMadanhmuc(quenmatkhauService.random());
        try {
            sanPhamService.addDanhmuc(danhMuc);
            return ResponseEntity.status(HttpStatus.CREATED).body(danhMuc);
        } catch (Exception e) {
            logger.error("Error adding category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/addThuongHieu")
    public ResponseEntity<ThuongHieu> addBrand(@RequestBody laytamDTO laytamDTO) {
        if (laytamDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ThuongHieu thuongHieu = new ThuongHieu();
        thuongHieu.setTenthuonghieu(laytamDTO.getName());
        thuongHieu.setNgaysua(LocalDate.now());
        thuongHieu.setTrangthai(1);
        thuongHieu.setMathuonghieu(quenmatkhauService.random());
        try {
            sanPhamService.addthuonghieu(thuongHieu);
            return ResponseEntity.status(HttpStatus.CREATED).body(thuongHieu);
        } catch (Exception e) {
            logger.error("Error adding brand: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/addTCSP")
    public ResponseEntity<?> addSanPham(@RequestBody SanPhamDTO sanPhamDTO) {
        try {
            System.out.println("Dữ liệu từ sanPhamDTO: " + sanPhamDTO);
            sanPhamService.saveSanPham(sanPhamDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Sản phẩm đã được thêm thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể thêm sản phẩm. Lỗi: " + e.getMessage());
        }
    }



    @PutMapping("/{id}/update-image")
    public ResponseEntity<?> updateImage(@PathVariable Integer id, @RequestParam("imagePath") MultipartFile imagePath) {
        System.out.println("Received image data for product " + id + ": " + imagePath.getOriginalFilename());

        // Gọi phương thức dịch vụ để lưu ảnh
        SanPham updatedSanPham = sanPhamService.updateImage(id, imagePath);
        if (updatedSanPham != null) {
            return ResponseEntity.ok(updatedSanPham);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại");
        }
    }




    @GetMapping("/temporarySanPhamChiTietList")
    public ResponseEntity<List<SanPhamChiTiet>> getTemporarySanPhamChiTietList() {
        List<SanPhamChiTiet> list = sanPhamService.getTemporarySanPhamChiTietList();
        return ResponseEntity.ok(list);
    }

    public sanphanController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }
    @GetMapping("/sanpham/chitiet")
    @ResponseBody
    public SanPham getSanPhamChiTiet(@RequestParam("id") Integer id) {
        return sanPhamService.Listtimkiemsp(id);
    }
    @PostMapping("/sanpham/update")
    public ResponseEntity<String> updateSanPhamChiTiet(@RequestBody List<SanPhamChiTietDTO> updates) {
        try {
            logger.info("Cập nhật sản phẩm: {}", updates);

            for (SanPhamChiTietDTO update : updates) {
                SanPhamChiTiet spChiTiet = sanPhamService.Listtimkiemspct(update.getIdSanPham());

                if (spChiTiet == null) {
                    return ResponseEntity.badRequest().body("Sản phẩm không tồn tại với ID: " + update.getIdSanPham());
                }

                spChiTiet.setSoluongsanpham(update.getSoLuong());
                spChiTiet.setDongia(update.getGia());

                // Chuyển đổi mauSac và kichThuoc từ String sang Integer
                Integer mauSacId = Integer.parseInt(update.getMauSac()); // Chuyển đổi từ String sang Integer
                Integer kichThuocId = Integer.parseInt(update.getKichThuoc()); // Chuyển đổi từ String sang Integer

                MauSac mauSac = sanPhamService.Listtimkiemms(mauSacId);
                KichThuoc kichThuoc = sanPhamService.Listtimkiemkt(kichThuocId);

                if (mauSac == null) {
                    return ResponseEntity.badRequest().body("Màu sắc không tồn tại với ID: " + mauSacId);
                }
                if (kichThuoc == null) {
                    return ResponseEntity.badRequest().body("Kích thước không tồn tại với ID: " + kichThuocId);
                }

                spChiTiet.setMauSac(mauSac);
                spChiTiet.setKichThuoc(kichThuoc);

                sanPhamService.addspct(spChiTiet); // Đảm bảo phương thức này thực hiện cập nhật đúng cách
            }
            return ResponseEntity.ok("Cập nhật thành công: " + updates.size() + " sản phẩm đã được cập nhật.");
        } catch (NumberFormatException e) {
            logger.warn("Có lỗi định dạng số: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID màu sắc hoặc kích thước không hợp lệ.");
        } catch (IllegalArgumentException e) {
            logger.warn("Có lỗi khi cập nhật sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Có lỗi xảy ra khi cập nhật sản phẩm: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật: " + e.getMessage());
        }
    }
    

        @PostMapping("/sanpham/updateStatus")
        public ResponseEntity<Void> updateProductStatus(@RequestBody Map<String, Object> statusUpdate) {
            try {
                // Kiểm tra xem các khóa có tồn tại không trước khi chuyển đổi giá trị
                if (statusUpdate.containsKey("trangthai") && statusUpdate.containsKey("sanPhamId")) {
                    int trangThai = Integer.parseInt(statusUpdate.get("trangthai").toString());
                    Integer sanPhamId = Integer.valueOf(statusUpdate.get("sanPhamId").toString());
                    System.out.println(trangThai == 1 ? "dangban":"ngungban");
                    sanPhamService.updateProductStatus(trangThai, sanPhamId);
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } catch (Exception e) {
                // Log lỗi chi tiết
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

    }






}





