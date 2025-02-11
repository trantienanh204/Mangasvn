package com.example.demo.Controller.thanhtoanonline;

import com.example.demo.DTO.KhachHangDTO;
import com.example.demo.DTO.TaiKhoanDTO;
import com.example.demo.entity.*;
import com.example.demo.respository.HoaDonCTRepository;
import com.example.demo.respository.HoaDonRepository;
import com.example.demo.respository.SanPhamChiTietRepository;
import com.example.demo.respository.VoucherRepository;
import com.example.demo.respository.nhanVien.DonHangRepository;
import com.example.demo.service.GioHangService;
import com.example.demo.service.HoaDonService;
import com.example.demo.service.TaiKhoanService;
import com.example.demo.service.VoucherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")

public class VnpayController {

    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private HoaDonCTRepository hoaDonCTRepository;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherService voucherService;
String codevc = null;
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submitOrder")
    public String submitOrder(@RequestParam("totalAmount") double totalAmount,
                              @RequestParam("orderInfo") String orderInfo,
                              @RequestParam("paymentMethod") String paymentMethod,
                              HttpServletRequest request, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if ("BankTransfer".equals(paymentMethod)) {
            String vnp_ReturnUrl = "http://localhost:8080/api/vnpay-payment";
            String vnpayUrl = vnPayService.createOrder((int) totalAmount, orderInfo);

            request.getSession().setAttribute("orderInfo", orderInfo);
            request.getSession().setAttribute("totalAmount", totalAmount);
            request.getSession().setAttribute("paymentMethod", paymentMethod);
            return "redirect:" + vnpayUrl;
        } else {
            return "redirect:/api/processInvoice?totalAmount=" + totalAmount + "&paymentMethod=" + paymentMethod + "&note=" + UriUtils.encodePath(orderInfo, StandardCharsets.UTF_8);
        }
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/processInvoice")
    public String processInvoice(@RequestParam double totalAmount,
                                 @RequestParam String paymentMethod,
                                 @RequestParam String note,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(required = false) String voucherCode, // Cho phép null để kiểm tra
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        String username = userDetails.getUsername();
        TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(username);

//        if (taiKhoanDto == null || taiKhoanDto.getKhachHangDTO() == null) {
//            return "/panda/login";
//        }

        KhachHang khachHang = mapToKhachHang(taiKhoanDto.getKhachHangDTO());
        int khachHangId = khachHang.getId();
        List<GioHang> cartItems = gioHangService.getCartItems(khachHangId);
        List<SanPhamChiTiet> sanPhamChiTiets = gioHangService.spctLisst;

        System.out.println("Danh sách sản phẩm chi tiết: " + sanPhamChiTiets);
        System.out.println("Danh sách giỏ hàng: " + cartItems);

        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống.");
            return "khachhang/GioHang";
        }

        List<GioHang> gioHangList = cartItems.stream()
                .filter(gioHang -> sanPhamChiTiets.stream()
                        .anyMatch(spct -> spct.getId().equals(gioHang.getSanPhamChiTiet().getId())))
                .collect(Collectors.toList());

        System.out.println("Received voucherCode: " + voucherCode);


        HoaDon hoaDon = createHoaDon(khachHang, gioHangList, totalAmount, note, paymentMethod,codevc);
        DonHang donHang = createDonHang(khachHang, hoaDon, totalAmount, note, paymentMethod, codevc);

        hoaDonService.save(hoaDon);
        donHangRepository.save(donHang);

        gioHangService.clearCart(khachHangId);

        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng của bạn đã được đặt thành công!");
        codevc=null;
        gioHangService.spctLisst.clear();
        return "redirect:/panda/trangchu";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save-voucher-code")
    public ResponseEntity<Void> saveVoucherCode(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String voucherCode = payload.get("voucherCode");
        HttpSession session = request.getSession();
        session.setAttribute("voucherCode", voucherCode);

        // Đọc lại voucherCode từ session ngay lập tức
        String savedVoucherCode = (String) session.getAttribute("voucherCode");
        if(savedVoucherCode != null){
            codevc = savedVoucherCode;
        }else{
            codevc = null;
        }

        System.out.println("Voucher Code from Session: " + savedVoucherCode);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
        @GetMapping("/api/check-voucher")
        public ResponseEntity<Map<String, Object>> checkVoucher(@RequestParam String code) {
            Map<String, Object> response = new HashMap<>();

            Optional<Voucher> voucher = voucherRepository.findByMa(code);
            Integer sl = Integer.parseInt(voucher.get().getSoLuong());
            if (voucher != null && sl > 0 && voucher.get().getNgayketthuc().isAfter(LocalDate.now())) {
                response.put("valid", true);
                response.put("discountType", voucher.get().isLoai() ? "%" : "VND");
                response.put("discountValue", Double.parseDouble(voucher.get().getMucGiam()));
            } else {
                response.put("valid", false);
            }

            return ResponseEntity.ok(response);
        }





    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vnpay-payment")
    public String handleVnPayReturn(HttpServletRequest request, Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        int paymentStatus = vnPayService.orderReturn(request);

        if (paymentStatus != 1) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán không thành công. Vui lòng thử lại.");
            return "redirect:/panda/ThanhToanThatBai";
        }

        // Lấy thông tin đơn hàng từ session (hoặc database)
        String orderInfo = (String) request.getSession().getAttribute("orderInfo");
        Double totalAmount = (Double) request.getSession().getAttribute("totalAmount");
        String paymentMethod = (String) request.getSession().getAttribute("paymentMethod");
        String voucherCode = request.getParameter("voucherCode"); // Lấy từ request thay vì session

        System.out.println("Received orderInfo: " + orderInfo);
        System.out.println("Received totalAmount: " + totalAmount);
        System.out.println("Received paymentMethod: " + paymentMethod);
        System.out.println("Received voucherCode: " + voucherCode); // Ghi nhật giá trị voucherCode

        // Kiểm tra các giá trị
        if (orderInfo == null || totalAmount == null || paymentMethod == null ) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại.");
            return "redirect:/panda/thatbai";
        }

        // Thực hiện xử lý hóa đơn
        return processInvoice(totalAmount, paymentMethod, orderInfo, userDetails, codevc, model, redirectAttributes);
    }

    @GetMapping("/check-voucher-code")
    @ResponseBody
    public String checkVoucherCode(HttpSession session ) {
        String savedVoucherCode = (String) session.getAttribute("voucherCode");
        System.out.println("Voucher Code from Session: " + savedVoucherCode);
        return savedVoucherCode != null ? "Voucher code in session: " + savedVoucherCode : "No voucher code in session";
    }




    private KhachHang mapToKhachHang(KhachHangDTO dto) {
        KhachHang khachHang = new KhachHang();
        khachHang.setId(dto.getId());
        khachHang.setMakhachhang(dto.getMakhachhang());
        khachHang.setTenkhachhang(dto.getTenkhachhang());
        khachHang.setSdt(dto.getSdt());
        khachHang.setDiachi(dto.getDiachi());
        return khachHang;
    }

    private HoaDon createHoaDon(KhachHang khachHang, List<GioHang> cartItems , double totalAmount, String note, String paymentMethod,String codevc) {
        HoaDon hoaDon = new HoaDon();
        String hd = hoaDonRepository.findMaxMaHoaDon();
        int demhd;
        if (hd == null) {
            demhd = 1; // Nếu chưa có hóa đơn nào thì bắt đầu từ 1
        } else {
            try {
                demhd = Integer.parseInt(hd.substring(2)) + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("Lỗi định dạng số từ mã hóa đơn: " + hd, e);
            }
        }
        String mahd = String.format("HD%03d", demhd);

        
        hoaDon.setMahoadon(mahd);
        hoaDon.setKhachHang(khachHang);
        hoaDon.setTongtien(BigDecimal.valueOf(totalAmount));

        Optional<Voucher> voucherOptional = voucherRepository.findByMa(codevc);

        if (voucherOptional.isPresent()) {
            Voucher voucher = voucherOptional.get();
            BigDecimal giamGia;

            if (voucher.isLoai()) {
                giamGia = BigDecimal.valueOf(totalAmount).multiply(new BigDecimal(voucher.getMucGiam())).divide(BigDecimal.valueOf(100));
            } else {
                giamGia = new BigDecimal(voucher.getMucGiam());
            }

            BigDecimal thanhTien = BigDecimal.valueOf(totalAmount).subtract(giamGia);
            if (thanhTien.compareTo(BigDecimal.ZERO) < 0) {
                thanhTien = BigDecimal.ZERO;
            }
            hoaDon.setThanhtien(thanhTien);
        } else {
            hoaDon.setThanhtien(BigDecimal.valueOf(totalAmount));
        }
        hoaDon.setNgaytao(LocalDate.now());
        hoaDon.setNgaymua(LocalDate.now());
        hoaDon.setHinhthucmuahang(true);
        hoaDon.setTrangthai(1);
        hoaDon.setDiaChi(khachHang.getDiachi());
        hoaDon.setGhiChu(note);
        hoaDon.setActive(true);
        if ("BankTransfer".equals(paymentMethod)) {
            hoaDon.setTttt(0);
        } else {
            hoaDon.setTttt(1);
        }

        List<HoaDonCT> chiTietList = new ArrayList<>();
        for (GioHang item : cartItems) {
            HoaDonCT chiTiet = new HoaDonCT();
            String hdct = hoaDonCTRepository.findMaxhoadonct();
            int demhdct;
            if (hdct == null) {
                demhdct = 1; // Nếu chưa có hóa đơn chi tiết nào thì bắt đầu từ 1
            } else {
                try {
                    demhdct = Integer.parseInt(hdct.substring(4)) + 1; // Phải cắt chuỗi đúng cách
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Lỗi định dạng số từ mã hóa đơn chi tiết: " + hdct, e);
                }
            }
            String mahdct = String.format("HDCT%03d", demhdct);
            chiTiet.setMahoadonct(mahdct);
            chiTiet.setSanPhamChiTiet(item.getSanPhamChiTiet());
            chiTiet.setSoluong(item.getSoluong());
            chiTiet.setDongia(BigDecimal.valueOf(item.getSanPhamChiTiet().getDongia()));
            chiTiet.setNgaytao(LocalDate.now());
            chiTiet.setTongtien(chiTiet.getDongia().multiply(BigDecimal.valueOf(chiTiet.getSoluong())));
            chiTiet.setHinhthucthanhtoan(paymentMethod);
            chiTiet.setHoaDon(hoaDon);
            chiTietList.add(chiTiet);

//            // Giảm số lượng sản phẩm trong kho
//            không sử dụng đc nữa
//            SanPhamChiTiet sanPhamChiTiet = item.getSanPhamChiTiet();
//            int soLuongConLai = sanPhamChiTiet.getSoluongsanpham() - item.getSoluong();
//            if (soLuongConLai < 0) {
//                throw new RuntimeException("Số lượng sản phẩm không đủ");
//            }
//            sanPhamChiTiet.setSoluongsanpham(soLuongConLai);
//            sanPhamChiTietRepository.save(sanPhamChiTiet);
        }
        hoaDon.setChiTietHoaDons(chiTietList);
        return hoaDon;
    }

    private DonHang createDonHang(KhachHang khachHang, HoaDon hoaDon, double totalAmount, String note, String paymentMethod, String voucherCode) {
        DonHang donHang = new DonHang();
        donHang.setHoaDon(hoaDon);
        donHang.setKhachHang(khachHang);
        donHang.setNgaytao(LocalDate.now());
        donHang.setTongtien(BigDecimal.valueOf(totalAmount));

        donHang.setDiaChi(khachHang.getDiachi());
        donHang.setSdt(khachHang.getSdt());
        donHang.setTrangThai("Chờ duyệt");
        donHang.setGhiChu(note);
        donHang.setLydohuy("");

        Optional<Voucher> vc = voucherRepository.findByMa(voucherCode);
        System.out.println("mã giảm :" +voucherCode);
        if (vc.isPresent()) {
            // Áp dụng mã giảm giá vào đơn hàng

            donHang.getHoaDon().setVoucher(vc.get());
            donHang.getHoaDon().setGiagiam(vc.get().getMucGiam() +" "+(vc.get().isLoai() == true ? "%" : "VNĐ"));
            // Lấy số lượng hiện tại của voucher và chuyển đổi thành số nguyên
            Voucher voucherEntity = vc.get();
            int currentQuantity;

            try {
                currentQuantity = Integer.parseInt(voucherEntity.getSoLuong()); // Chuyển đổi từ String sang int
            } catch (NumberFormatException e) {
                System.out.println("Số lượng voucher không hợp lệ: " + voucherEntity.getSoLuong());
                return null; // Ngừng xử lý và trả về null nếu số lượng không hợp lệ
            }

            int slvcconlai = currentQuantity - 1;

            // Kiểm tra nếu số lượng còn lại < 0, tránh số âm
            if (slvcconlai < 0) {
                System.out.println("Số lượng voucher không đủ");
                return null; // Ngừng xử lý và trả về null nếu số lượng không đủ
            }

            // Cập nhật lại số lượng voucher trong cơ sở dữ liệu
            voucherEntity.setSoLuong(String.valueOf(slvcconlai)); // Chuyển đổi từ int sang String
            voucherRepository.save(voucherEntity);

            System.out.println("Số lượng voucher còn lại: " + slvcconlai);
        } else {
            System.out.println("Mã giảm giá không tồn tại");
        }

        // Đặt giá trị trangthaioffline dựa trên phương thức thanh toán
        if ("BankTransfer".equals(paymentMethod)) {
            donHang.setTrangthaioffline(true);

        } else {
            donHang.setTrangthaioffline(false);

        }

        return donHang;
    }

}
