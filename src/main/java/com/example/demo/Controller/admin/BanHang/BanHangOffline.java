
package com.example.demo.Controller.admin.BanHang;

import com.example.demo.DTO.HoaDonCTDTO;
import com.example.demo.DTO.NhanVienDTO;
import com.example.demo.DTO.TaiKhoanDTO;
import com.example.demo.entity.*;
import com.example.demo.respository.*;
import com.example.demo.respository.nhanVien.DonHangRepository;
import com.example.demo.service.DonHangService;
import com.example.demo.service.TaiKhoanService;
import com.example.demo.services.BanHangService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/panda/banhangoffline")
public class BanHangOffline {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    BanHangService banHangService;
    @Autowired
    HoaDonCTRepository hoaDonCTRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    NhanVienRespository nhanVienRespository;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    TaiKhoanService taiKhoanService;
    @Autowired
    DonHangRepository donHangRepository;
    @Autowired
    DonHangService donHangService;


    private Integer idhd;
    private Integer idmax;;

    @GetMapping("/hienthi")
    public String show(Model model) {
        String role = "nhanvien";
        model.addAttribute("role", role);
        List<HoaDon> hoaDon = hoaDonRepository.findHoaDonsWithNullId();
        List<Voucher> voucher = voucherRepository.findAll();
        List<SanPhamChiTiet> spct = sanPhamChiTietRepository.findAll();
        List<Voucher> validVouchers = new ArrayList<>();
        List<KhachHang> listkh = khachHangRepository.timkhachhang();
        for (Voucher v : voucher) {
            try {
                int soLuong = Integer.parseInt(v.getSoLuong());
                if (soLuong > 0) {
                    validVouchers.add(v);
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi : " + v.getMa());
            }
        }
        spct.forEach(sp -> {
            SanPham sanPham = sp.getSanPham();
            if (sanPham != null && sanPham.getAnhsp() != null) {
                String base64Image = Base64.getEncoder().encodeToString(sanPham.getAnhsp());
                sanPham.setBase64Image(base64Image);
            }
        });
        model.addAttribute("hoaDons", hoaDon);
        model.addAttribute("lsvoucher", validVouchers);
        model.addAttribute("lsspct", spct);
        model.addAttribute("listkh", listkh);
        return "admin/BanHang/BanHangOffline";
    }

    @GetMapping()
    public String hienthi(Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        List<HoaDon> hoaDon = hoaDonRepository.findHoaDonsWithNullId();
        List<Voucher> voucher = voucherRepository.findAll();
        List<SanPhamChiTiet> spct = sanPhamChiTietRepository.findAll();
        List<Voucher> validVouchers = new ArrayList<>();
        List<KhachHang> listkh = khachHangRepository.timkhachhang();
        for (Voucher v : voucher) {
            try {
                int soLuong = Integer.parseInt(v.getSoLuong());
                if (soLuong > 0) {
                    validVouchers.add(v);
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi : " + v.getMa());
            }
        }
        List<HoaDon> hoaDonMax = hoaDonRepository.findHoaDonsDesc();
        int id;
        if (hoaDonMax != null && !hoaDonMax.isEmpty()) {
            id = hoaDonMax.get(0).getId();
        } else {
            id = 1;
        }
        this.idmax = id;
        model.addAttribute("hoaDons", hoaDon);
        model.addAttribute("lsvoucher", validVouchers);
        model.addAttribute("lsspct", spct);
        model.addAttribute("listkh", listkh);
        if (hoaDonMax == null || hoaDonMax.size() == 0) {
            return "admin/BanHang/BanHangOffline";
        } else {
            return "redirect:/panda/banhangoffline/muahang/" + id;
        }

    }

    @GetMapping("/timkiem")
    @ResponseBody
    public List<String> getSuggestions() {
        return sanPhamChiTietRepository.findAll().stream()
                .map(sp -> String.format("%s - %s - %s - %s",
                        sp.getSanPham().getMasp(),
                        sp.getSanPham().getTensp(),
                        sp.getMauSac().getTen(),
                        sp.getKichThuoc().getTen()))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchSanPham(@RequestParam("keyword") String keyword) {
        List<String> results = new ArrayList<>();
        try {
            results = banHangService.findByTenSanPham(keyword).stream()
                    .filter(sp -> sp.getSanPham() != null && sp.getMauSac() != null
                            && sp.getKichThuoc() != null && sp.getSoluongsanpham() > 0)
                    .map(sp -> {
                        String base64Image = "";
                        if (sp.getSanPham().getAnhsp() != null) {
                            base64Image = Base64.getEncoder().encodeToString(sp.getSanPham().getAnhsp());
                        }
                        return String.format("%s - %s - %s - %s - %s - %s - %s - %s",
                                sp.getId(),
                                sp.getSanPham().getTensp(),
                                sp.getMauSac().getTen(),
                                sp.getKichThuoc().getTen(),
                                sp.getDongia(),
                                sp.getSanPham().getChatLieu().getTenChatLieu(),
                                sp.getSoluongsanpham(),
                                base64Image
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/searchkh")
    public ResponseEntity<List<String>> searchkhachhang(@RequestParam("keyword") String keyword) {
        List<String> results = new ArrayList<>();
        try {
            results = banHangService.findBysdt(keyword).stream()
                    .filter(kh -> kh.getTenkhachhang() != null && kh.getMakhachhang() != null && kh.getSdt() != null)
                    .map(sp -> String.format("%s - %s - %s - %s ",
                            sp.getId(),
                            sp.getMakhachhang(),
                            sp.getTenkhachhang(),
                            sp.getSdt()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/muahang/{id}")
    public String muahang(@PathVariable(value = "id",required = false) int idhoadon, Model model) {
        this.idhd = idhoadon;
        List<HoaDon> hoaDons = hoaDonRepository.findAll();
        // Đánh dấu hóa đơn được chọn là active
        for (HoaDon hoaDon : hoaDons) {
            if (hoaDon.getId() == idhoadon) {
                hoaDon.setActive(true);
            } else {
                hoaDon.setActive(false);
            }
            hoaDonRepository.save(hoaDon);
        }

        List<HoaDonCT> hoaDonCTList = hoaDonCTRepository.findhoadonct(idhoadon);
        HoaDon lshd = hoaDonRepository.findById(idhoadon).orElse(null);
//        BigDecimal tongTien = hoaDonCTRepository.TongTienByHoaDonId(idhd);
        BigDecimal tt = tongtien(idhd);
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        String tongTien = decimalFormat.format(tt);
        String thanhtien = decimalFormat.format(tt);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("tongtien", tt);
        model.addAttribute("thanhtien", thanhtien);
        model.addAttribute("thanhTien", tt);
        model.addAttribute("hoaDonCTList", hoaDonCTList);
        model.addAttribute("idhoadon", idhoadon);
        model.addAttribute("mahd", lshd.getMahoadon());
        return "forward:/panda/banhangoffline/hienthi";
    }

    @GetMapping("/find")
    @ResponseBody
    public List<String> getfindten(@RequestParam("keyword") String keyword) {
        System.out.println("keyword: " + keyword);
        List<String> suggestions = new ArrayList<>();
        try {
            suggestions = banHangService.findByTenSanPham(keyword).stream()
                    .filter(sp -> sp.getSanPham() != null && sp.getMauSac() != null && sp.getKichThuoc() != null)
                    .map(sp -> String.format("%s - %s - %s - %s - %s",
                            sp.getId(),
                            sp.getSanPham().getTensp(),
                            sp.getMauSac().getTen(),
                            sp.getKichThuoc().getTen(),
                            sp.getDongia()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    @GetMapping("/hoadon")
    public ResponseEntity<List<HoaDon>> getEmptyHoaDons() {
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsWithNullId();
        return ResponseEntity.ok(hoaDons);
    }

    private static final HashMap<String, Integer> demstt = new HashMap<>();

    @PostMapping("/taohd")
    public ResponseEntity<Map<String, String>> taohoadon(Model model) {
        String hd = hoaDonRepository.findMaxMaHoaDon();
        int demhd;
        if (hd == null) {
            demhd = 1;
        } else {
            demhd = Integer.parseInt(hd.substring(2)) + 1;
        }
        String mahd = String.format("HD%03d", demhd);

        // Tạo mã hóa đơn
        HoaDon newhd = new HoaDon();
        newhd.setMahoadon(mahd);
        newhd.setActive(false);
        newhd.setNgaytao(LocalDate.now());
        hoaDonRepository.save(newhd);
        int idhdon = newhd.getId();
        Map<String, String> response = new HashMap<>();
        response.put("mahoadon", mahd);
        BigDecimal tongTien = hoaDonCTRepository.TongTienByHoaDonId(idhdon);
        model.addAttribute("tongTien", tongTien);
        return ResponseEntity.ok(response);
    }

    BigDecimal tongtien(int id) {
        this.idhd = id;
        return hoaDonCTRepository.TongTienByHoaDonId(idhd);
    }


    @PostMapping("/taohdct")
    public ResponseEntity<String> createHoaDonCT(@RequestBody HoaDonCTDTO dto,
                                                 Model model,RedirectAttributes redirectAttributes) {
        HoaDonCT hoaDonCT = new HoaDonCT();
        Map<String, String> response = new HashMap<>();
        System.out.println("id hd " + idhd);
        if(idhd == null){
            return ResponseEntity.badRequest().body("Chưa chọn hóa đơn!");
        }
        hoaDonCT.setHoaDon(new HoaDon(idhd));
        hoaDonCT.setSanPhamChiTiet(new SanPhamChiTiet(dto.getIdSanPhamCT()));
        hoaDonCT.setDongia(dto.getDonGia());
        hoaDonCT.setSoluong(dto.getSoLuong());
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(dto.getIdSanPhamCT()).get();
        HoaDonCT existingHoaDonCT = hoaDonCTRepository.findByHoaDonIdAndSanPhamId(idhd, dto.getIdSanPhamCT());
        if (existingHoaDonCT != null) {
            int soluongban = existingHoaDonCT.getSoluong() + 1;
            if(soluongban > spct.getSoluongsanpham()){
                System.out.println("số lượng không đủ");
                return ResponseEntity.badRequest().body("Số lượng vượt quá số lượng tồn kho");
            }
            existingHoaDonCT.setSoluong(soluongban);
            existingHoaDonCT.setId(existingHoaDonCT.getId());
            BigDecimal totalPrice = existingHoaDonCT.getDongia().multiply(BigDecimal.valueOf(existingHoaDonCT.getSoluong()));
            existingHoaDonCT.setTongtien(totalPrice);
            System.out.println("id hdct : " + existingHoaDonCT.getId());
            hoaDonCTRepository.save(existingHoaDonCT);
            return ResponseEntity.ok("Thành công !");
        }
        // Nếu không tồn tại, tạo mới
        BigDecimal donGia = dto.getDonGia();
        int soLuong = dto.getSoLuong();
        BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        hoaDonCT.setTongtien(thanhTien);
        hoaDonCT.setNgaytao(LocalDate.now());
        hoaDonCTRepository.save(hoaDonCT);
        return ResponseEntity.ok("Thành công !");
    }

    @PostMapping("/taohdctqr")
    public ResponseEntity<?> taohdct(@RequestBody HoaDonCTDTO dto,Model model,RedirectAttributes redirectAttributes) {
        String maspct = dto.getMaspct();
        SanPhamChiTiet spct = sanPhamChiTietRepository.findByMaspct(maspct);
        System.out.println("mã spct" + maspct);
        HoaDonCT hoaDonCT = new HoaDonCT();
        if(spct == null){
            redirectAttributes.addFlashAttribute("loi", "Không tìm thấy sản phẩm");
            return ResponseEntity.badRequest().body(Map.of("message","Không tìm thấy sản phẩm!"));
        }
        if(idhd == null){
            redirectAttributes.addFlashAttribute("loi", "Chưa chọn hóa đơn!");
            return ResponseEntity.badRequest().body(Map.of("message","Chưa chọn hóa đơn!"));
        }
        if(spct == null){

            redirectAttributes.addFlashAttribute("loi", "Không tìm thấy sản phẩm");
            return ResponseEntity.badRequest().body(Map.of("message","Không tìm thấy sản phẩm!"));

        }
        int soLuongTonKho = spct.getSoluongsanpham();

        if (soLuongTonKho <= 0) {
            redirectAttributes.addFlashAttribute("loi", "Sản phẩm hết hàng!");
            return ResponseEntity.badRequest().body(Map.of("message", "Sản phẩm hết hàng!"));
        }

        hoaDonCT.setHoaDon(new HoaDon(idhd));
        hoaDonCT.setSanPhamChiTiet(spct);
        hoaDonCT.setDongia(BigDecimal.valueOf(spct.getDongia()));
        hoaDonCT.setSoluong(1);
        HoaDonCT existingHoaDonCT = hoaDonCTRepository.findByHoaDonIdAndSanPhamId(idhd, spct.getId());
        if (existingHoaDonCT != null) {
            int soLuongMoi = existingHoaDonCT.getSoluong() + 1;
            if (soLuongMoi > soLuongTonKho) {
                redirectAttributes.addFlashAttribute("loi", "Số lượng nhập vượt quá số lượng tồn kho");
                return ResponseEntity.badRequest().body(Map.of("message", "Số lượng yêu cầu vượt quá tồn kho!"));
            }
            existingHoaDonCT.setSoluong(soLuongMoi);
            existingHoaDonCT.setId(existingHoaDonCT.getId());
            BigDecimal totalPrice = existingHoaDonCT.getDongia().multiply(BigDecimal.valueOf(existingHoaDonCT.getSoluong()));
            existingHoaDonCT.setTongtien(totalPrice);
            System.out.println("id hdct : " + existingHoaDonCT.getId());
            hoaDonCTRepository.save(existingHoaDonCT);
            return ResponseEntity.ok(Map.of("ok", "Thành công!"));
        }
        // Nếu không tồn tại, tạo mới
        BigDecimal donGia = BigDecimal.valueOf(spct.getDongia());
        int soLuong = 1;
        BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        hoaDonCT.setTongtien(thanhTien);
        hoaDonCT.setNgaytao(LocalDate.now());
        hoaDonCTRepository.save(hoaDonCT);
        return ResponseEntity.ok(Map.of("message", "Thành công!"));
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateProduct(@RequestBody HoaDonCTDTO dto, Model model, RedirectAttributes redirectAttributes) {
        HoaDonCT hoaDonCT = hoaDonCTRepository.findById(dto.getIdHoadon())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chi tiết"));
        System.out.println("idspct = " + dto.getIdSanPhamCT());
        Optional<SanPhamChiTiet> spctOptional = sanPhamChiTietRepository.findById(dto.getIdSanPhamCT());
        if (spctOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm chi tiết.");
        }
        SanPhamChiTiet spct = spctOptional.get();
        if (dto.getSoLuong() > spct.getSoluongsanpham()) {
            return ResponseEntity.badRequest().body("Số lượng nhập vượt quá số lượng tồn kho");
        }
        if (dto.getSoLuong() <= 0) {
            return ResponseEntity.badRequest().body("Số lượng phải lớn hơn 0");
        }

        hoaDonCT.setSoluong(dto.getSoLuong());
        hoaDonCT.setTongtien(dto.getThanhTien());
        hoaDonCT.setTrangthai(1);
        BigDecimal tongTien = tongtien(idhd);
        System.out.println("tongTien :"+ tongTien);
        System.out.println("Thành tièn:"+  dto.getThanhTien());
        System.out.println("sl :"+  dto.getSoLuong());
        hoaDonCTRepository.save(hoaDonCT);
        return ResponseEntity.ok("Cập nhật thành công" );
    }

    @GetMapping("/chonkh")
    public ResponseEntity<Map<String, String>> chonkh(@RequestParam(value = "id", defaultValue = "0") Integer id,
                                                      Model model) {
        Map<String, String> response = new HashMap<>();
        KhachHang kh = khachHangRepository.findById(id).orElse(null);
        response.put("tenkh", kh.getTenkhachhang());
        response.put("id", String.valueOf(kh.getId()));
        response.put("sdt", kh.getSdt());

        response.put("diachi", kh.getDiachi() != null ? kh.getDiachi() :"Trống");

        response.put("idTinhThanhPho", kh.getTinhtp() != null ? kh.getTinhtp() : "");
        response.put("idQuanHuyen", kh.getQuanhuyen() != null ? kh.getQuanhuyen() : "");
        response.put("idXaPhuong", kh.getXaphuong() != null ? kh.getXaphuong() : "");
        response.put("tentinh", kh.getTentinh() != null ? kh.getTentinh() : "");
        response.put("tenhuyen", kh.getTenhuyen() != null ? kh.getTenhuyen() : "");
        response.put("tenxa", kh.getTenxa() != null ? kh.getTenxa() : "");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/selectvc")
    public ResponseEntity<Map<String, String>> chonVoucher(@RequestParam(value = "id", defaultValue = "0") String id,
                                                           Model model) {
        System.out.println("Voucher ID nhận từ client: " + id);
        Map<String, String> response = new HashMap<>();
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        BigDecimal tt = tongtien(idhd);
        BigDecimal thanhtien = BigDecimal.ZERO;
        String loai = "0";

        Optional<Voucher> checkvoucher = voucherRepository.findByMa(id);
        if (checkvoucher.isPresent() && checkvoucher.get().getTrangThai() == 1 &&
                Integer.parseInt(checkvoucher.get().getSoLuong()) >= 1) {
            Voucher voucher = checkvoucher.get();
            System.out.println("Voucher ID nhận từ client: " + id);
            int mucgiam = Integer.parseInt(voucher.getMucGiam());
            int giamin = Integer.parseInt(voucher.getDieuKien());
            if (tt.compareTo(new BigDecimal(giamin)) < 0) {
                response.put("error", "Tổng tiền phải lớn hơn " + decimalFormat.format(new BigDecimal(giamin)) + " để áp dụng voucher.");
                return ResponseEntity.badRequest().body(response);
            }
            if (voucher.isLoai()) {
                BigDecimal mucGiamBD = new BigDecimal(mucgiam);
                BigDecimal sophantram = tt.divide(new BigDecimal(100), RoundingMode.HALF_UP).multiply(mucGiamBD);
                loai = voucher.getMucGiam() + "% ";
            } else {
                thanhtien = tt.subtract(new BigDecimal(mucgiam));
                BigDecimal giamgia = new BigDecimal(voucher.getMucGiam());
                if(thanhtien.compareTo(giamgia) <= 0){
                    thanhtien = BigDecimal.ZERO;
                    System.out.println("0dd" +thanhtien);
                    System.out.println("mức giảm vnd : " +giamgia);
                }else{
                    thanhtien = tt.subtract(giamgia);
                    System.out.println("tiền gốc " +thanhtien);
                }
                String formattedgiamgia = decimalFormat.format(giamgia);
                loai = formattedgiamgia + " VND ";
            }
        } else {
            response.put("error", "Voucher không tồn tại hoặc đã hết");
            System.out.println("Voucher không tồn tại.");
            thanhtien = tt;
            System.out.println("không vc :" +thanhtien);
            loai = "0";
            return ResponseEntity.badRequest().body(response);
        }
        String formattedThanhtien = decimalFormat.format(thanhtien);
        System.out.println("a"+formattedThanhtien+"a");

        response.put("thanhtien", formattedThanhtien);
//        response.put("thanhtien", formattedThanhtien);
        response.put("thanhTien", String.valueOf(thanhtien));
        response.put("tt", String.valueOf(tt));
        response.put("mavocher", checkvoucher.isPresent() ? checkvoucher.get().getMa() : "");
        response.put("idvoucher", checkvoucher.isPresent() ? String.valueOf(checkvoucher.get().getId()) : "0");
        response.put("mucgiam", loai);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteHoaDonCT(@RequestParam Integer idHoaDonCT,RedirectAttributes redirectAttributes,Model model) {
        System.out.println("ID hd : " + idHoaDonCT);
        try {
            HoaDonCT hoaDonCT = hoaDonCTRepository.findById(idHoaDonCT)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Hóa đơn chi tiết"));
            hoaDonCTRepository.delete(hoaDonCT);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xóa");
        }
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/thanhtoan")
    public String thanhtoan(
            @RequestParam(value = "idhoadon",required = false) Integer idhoadon,
            @RequestParam(value = "idvoucher", required = false) Integer idvoucher,
            @RequestParam(value = "thanhtien",defaultValue = "0") BigDecimal thanhtien,
            @RequestParam(value = "tongtien",defaultValue = "0") BigDecimal tongtien,
            @RequestParam("sdt") String sdt,
            @RequestParam("giaohang") String giaohang,
//            @RequestParam("tinh") String tinh,
//            @RequestParam("huyen") String huyen,
//            @RequestParam("xa") String xa,
//            @RequestParam("tentinh") String tentinh,
//            @RequestParam("tenhuyen") String tenhuyen,
//            @RequestParam("tenxa") String tenxa,

            @RequestParam(value = "diachicuthe",defaultValue = "Hà Nội") String diachicuthe,

            @RequestParam(value = "ghichu",defaultValue = "trống") String ghichu,

            @RequestParam(value = "tenkh",defaultValue= "Khách lẻ") String tenkh,
            @RequestParam("mucgiam") String giagiam,
            @RequestParam(value = "checkbox-tt",defaultValue = "1") String checkbox,
            RedirectAttributes redirectAttributes,
            Model model ,
            @AuthenticationPrincipal UserDetails userDetails

    ) {
        HoaDon hd = hoaDonRepository.finid(idhoadon);
        DonHang dh = new DonHang();
        if (hd == null) {
            System.out.println("hd trống ");
            redirectAttributes.addFlashAttribute("loi", "Chưa chọn hóa đơn");
            return "redirect:/panda/banhangoffline";
        }
//        NhanVien nhanVien = nhanVienRespository.findById(3).orElse(null);

        if(giaohang.equals("1")){
            if(sdt.isBlank() || diachicuthe.isBlank()){
                redirectAttributes.addFlashAttribute("loi", "Chưa nhập đầy đủ thông tin người nhận");
                return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
            }
            dh.setTrangThai("Đã duyệt");
        }

        List<HoaDonCT> lshdct = hoaDonCTRepository.findhdct(idhoadon);
        if (lshdct == null || lshdct.isEmpty()) {
            redirectAttributes.addFlashAttribute("loi", "Chưa có sản phẩm nào");
            model.addAttribute("loi", "Chưa có sản phẩm nào");
            return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
        }


        if(giaohang.equals("1")){
            if(sdt.trim().isEmpty() || diachicuthe.trim().isEmpty()){

                redirectAttributes.addFlashAttribute("loi", "Chưa nhập đầy đủ thông tin người nhận");
                return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
            }
            dh.setTrangThai("Đã duyệt");
        }

        if(checkbox.equals("0") && giaohang.equals("1") ){
            dh.setTrangthaioffline(true);
            hd.setTttt(0);
        }else{
            dh.setTrangthaioffline(false);
            hd.setTttt(1);
        }



        if (sdt.isBlank()) {
            KhachHang kh1 = khachHangRepository.findById(6).orElse(null);

            if (kh1 != null) {
                System.out.println("Khách hàng mặc định");
                hd.setKhachHang(kh1);
                dh.setKhachHang(kh1);
            } else {
                System.out.println("Không tìm thấy khách hàng mặc định!");
            }
        } else {
            KhachHang kh = khachHangRepository.findBySdt(sdt);
            if (kh == null) {
                if (!sdt.matches("^0[0-9]{9}$")) {
                    redirectAttributes.addFlashAttribute("loi", "Số sai định dạng");
                    return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
                }
                kh = new KhachHang();
                String maKhachHang = "KH" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                kh.setMakhachhang(maKhachHang);
                kh.setTenkhachhang(tenkh);
                kh.setTrangthai(1);
                kh.setDelete(true);
                kh.setTinhtrang(true);
                kh.setSdt(sdt);
                kh.setDiachi(diachicuthe);
//                kh.setTinhtp(tinh);
//                kh.setQuanhuyen(huyen);
//                kh.setXaphuong(xa);
//                kh.setTentinh(tentinh);
//                kh.setTenhuyen(tenhuyen);
//                kh.setTenxa(tenxa);
                System.out.println("Thêm khách hàng mới");
                khachHangRepository.save(kh);
            } else {
                kh.setTenkhachhang(tenkh);
                kh.setSdt(sdt);
                kh.setDiachi(diachicuthe);

                kh.setTrangthai(1);
                kh.setTinhtrang(true);
                kh.setDelete(true);

//                kh.setTinhtp(tinh);
//                kh.setQuanhuyen(huyen);
//                kh.setXaphuong(xa);
//                kh.setTentinh(tentinh);
//                kh.setTenhuyen(tenhuyen);
//                kh.setTenxa(tenxa);
                System.out.println("Cập nhật khách hàng");
                khachHangRepository.save(kh);
            }
            hd.setKhachHang(kh);
            dh.setKhachHang(kh);

        }
        Voucher vc = null;
        if (idvoucher != null && idvoucher > 0) {
            vc = voucherRepository.findById(idvoucher).orElse(null);
            if (vc == null || Integer.parseInt(vc.getSoLuong()) <= 0) {
                System.out.println("vc trống ");
                redirectAttributes.addFlashAttribute("loi", "Voucher không hợp lệ hoặc đã hết số lượng");
                return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
            }

            KhachHang checkkh = khachHangRepository.findBySdt(sdt);
            if (checkkh != null && vc != null) {
                boolean checkmavoucher = hoaDonRepository.checkmavoucher(vc.getId(), checkkh.getId());
                if (checkmavoucher) {
                    redirectAttributes.addFlashAttribute("loi", "Voucher đã được sử dụng cho khách hàng này");
                    System.out.println("vc đã dùng ");
                    return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
                }
            }


            if (vc != null && Integer.parseInt(vc.getSoLuong()) > 0) {
                int currentQuantity = Integer.parseInt(vc.getSoLuong());
                currentQuantity -= 1;
                vc.setSoLuong(String.valueOf(currentQuantity));
                voucherRepository.save(vc);
            }
        }

        for (HoaDonCT hdct : lshdct) {
            SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
            int soLuongBan = hdct.getSoluong();
            int soLuongTon = spct.getSoluongsanpham();
            if (spct != null) {
                if(soLuongTon == 0){
                    redirectAttributes.addFlashAttribute("loi", "Sản phẩm : "+spct.getSanPham().getTensp() +spct.getMauSac().getTen()
                            + " " + spct.getKichThuoc().getTen() + " đã hết hàng");
                    return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
                }
                if(soLuongBan <= 0){
                    System.out.println("số lg bán");
                    redirectAttributes.addFlashAttribute("loi", "Số lượng sản phẩm không hợp lệ");
                    return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
                }
                if(soLuongTon < soLuongBan){
                    redirectAttributes.addFlashAttribute("loi", "Sản phẩm : "+spct.getSanPham().getTensp() +spct.getMauSac().getTen()
                            + " " + spct.getKichThuoc().getTen() + " không đủ hàng");
                    System.out.println("số lg k đủ");
                    return "redirect:/panda/banhangoffline/muahang/" + idhoadon;
                }
                spct.setSoluongsanpham(soLuongTon - soLuongBan);
                sanPhamChiTietRepository.save(spct);
            }
        }

        String username = userDetails.getUsername();
        TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(username);
        if (taiKhoanDto == null || taiKhoanDto.getNhanVienDTO() == null) {
            return "redirect:/panda/login";
        }
       NhanVien nhanVien = mapToNhanvien(taiKhoanDto.getNhanVienDTO());

        hd.setNhanVien(nhanVien);
        hd.setVoucher(vc);
        hd.setThanhtien(thanhtien);
        hd.setGiagiam(giagiam);
        hd.setTongtien(tongtien);
        hd.setTrangthai(1);
        hd.setNgaymua(LocalDate.now());
        hd.setGhiChu(ghichu);



        hoaDonRepository.save(hd);

        dh.setHoaDon(hd);
        dh.setNhanVien(nhanVien);

        dh.setDiaChi(diachicuthe);
        dh.setGhiChu(ghichu);
        dh.setTongtien(tongtien);
        dh.setSdt(sdt);
        dh.setNgaytao(LocalDate.now());
        dh.setHinhthuc(1);
        donHangRepository.save(dh);
        this.idhd = null;
        redirectAttributes.addFlashAttribute("message","Thanh toán thành công");
        model.addAttribute("message","Thanh toán thành công");
        return "redirect:/panda/banhangoffline";
    }

    private NhanVien mapToNhanvien(NhanVienDTO dto) {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setId(dto.getId());
        nhanVien.setManhanvien(dto.getManhanvien());
        nhanVien.setTennhanvien(dto.getTennhanvien());
        return nhanVien;
    }
}


