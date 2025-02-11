package com.example.demo.Controller.admin;


import com.example.demo.entity.ThuongHieu;
import com.example.demo.entity.Voucher;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.VoucherRepository;
import com.example.demo.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/panda/voucher")
public class GiamGiaController {
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    KhachHangRepository khachHangRespository;
    @Autowired
    VoucherService voucherService;

    @GetMapping("/hienthi")
    public String viewKT(@RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "ma", required = false) String ma,
                         @RequestParam(value = "ten", required = false) String ten,
                         @RequestParam(value = "startDate", required = false)@DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
                         @RequestParam(value = "endDate", required = false)@DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
                         @RequestParam(value = "trangThai", required = false) Integer trangThai,
                         Model model) {
//        slide bar
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
//        check ngày hết hạn
        voucherService.updateVoucherStatus();
//        Phân trang
        if (page < 0) {
            page = 0;
        }
        Page<Voucher> listVC  = voucherService.hienThiVC(page, ma, ten,startDate,endDate, trangThai);
        model.addAttribute("totalPage", listVC.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("listvc", listVC.getContent());
        model.addAttribute("ma", ma);
        model.addAttribute("ten", ten);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("chonmavc", voucherRepository.chonVC());
        model.addAttribute("listkh", khachHangRespository.dskhhoatdong());
        model.addAttribute("pageSize", listVC.getSize());
//        model.addAttribute("Voucher", new Voucher());
        return "/admin/GiamGia";
    }

    @GetMapping("/viewadd")
    public String view(Model model) {
        model.addAttribute("Voucher", new Voucher());
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "admin/QLSP/ADD/AddVC";
    }

    @PostMapping("/add")
    public String AddVC(@ModelAttribute("Voucher") Voucher voucher, Model model,
                            RedirectAttributes redirectAttributes) {
        //slide bar
        String role = "admin"; // Lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        LocalDate today = LocalDate.now();
        String regexma = "^[a-zA-Z0-9]+$";
        String regex = "^[\\p{L}0-9\\s]+$";

        Pattern patternma = Pattern.compile(regexma);
        Pattern pattern = Pattern.compile(regex);

        Matcher mavcMatcher = patternma.matcher(voucher.getMa());
        Matcher tenvcMatcher = pattern.matcher(voucher.getTen());
        Matcher motavcMatcher = pattern.matcher(voucher.getMota());
//        String soLuongStr = voucher.getSoLuong().trim();

        // mã voucher
        if (voucher.getMa().isEmpty()) {
            redirectAttributes.addFlashAttribute("Erorr", "Thêm thất bại!");
            model.addAttribute("errorma", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        }
        if (!mavcMatcher.matches()) {
            model.addAttribute("errorma", "Mã chỉ được chứa chữ và số");
            redirectAttributes.addFlashAttribute("Erorr", "Thêm thất bại!");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.getMa() == null || voucher.getMa().length() < 5 || voucher.getMa().length() > 14) {
            model.addAttribute("errorma", "Mã phải lớn hơn 4 ký tự và nhỏ hơn 15 ký tự");
            redirectAttributes.addFlashAttribute("Erorr", "Thêm thất bại!");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucherRepository.existsVoucherByMa(voucher.getMa())) {
            model.addAttribute("errorma", "Mã đã tồn tại");
            redirectAttributes.addFlashAttribute("Erorr", "Thêm thất bại!");
            return "admin/QLSP/ADD/AddVC";
        }
        //tên voucher
        if (voucher.getTen().isEmpty() || voucher.getMa().isEmpty()) {
            model.addAttribute("errorten", "Không được để trống");
            redirectAttributes.addFlashAttribute("Erorr", "Thêm thất bại!");
            return "admin/QLSP/ADD/AddVC";
        }
        if (!tenvcMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.getTen() == null || voucher.getTen().length() <= 1 || voucher.getTen().length() >= 255) {
            model.addAttribute("errorten", "Tên phải lớn hơn 1 ký tự và nhỏ hơn 255 ký tự");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucherRepository.existsVoucherByTen(voucher.getTen())) {
            model.addAttribute("errorten", "Tên đã tồn tại");
            return "admin/QLSP/ADD/AddVC";
        }
        //ngày bắt đầu
        if (voucher.getNgaybatdau() == null) {
            model.addAttribute("errornbd", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        }
        if (today.isBefore(voucher.getNgaybatdau())) {
            // Nếu ngày hiện tại bé hơn ngày bắt đầu, trạng thái là "Sắp hoạt động"
            voucher.setTrangThai(0);
            model.addAttribute("warning", "Chú ý: Voucher chưa thể hoạt động vì ngày bắt đầu chưa đến, sẽ tự động bắt đầu hoạt động khi đến ngày!");
        }
        //ngày kết thúc
        if (voucher.getNgayketthuc() == null) {
            model.addAttribute("errornkt", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.getNgayketthuc().isBefore(voucher.getNgaybatdau()) || voucher.getNgayketthuc().isEqual(voucher.getNgaybatdau())) {
            model.addAttribute("errornkt", "Ngày kết thúc phải lớn hơn ngày bắt đầu");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.getNgayketthuc().isBefore(today)) {
            voucher.setTrangThai(0); // Đặt trạng thái thành "Sắp hpạt động"
            model.addAttribute("errornkt", "Voucher không thể tạo vì đã qua ngày kết thúc");
            return "admin/QLSP/ADD/AddVC"; // Trả về trang với thông báo lỗi
        }
        //số lượng
        if (voucher.getSoLuong().isEmpty()) {
            model.addAttribute("errorsl", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.getSoLuong() == null || voucher.getSoLuong().length() >= 30) {
            model.addAttribute("errorsl", "Số lượng phải lớn hơn 0 ký tự và nhỏ hơn 30 ký tự");
            return "admin/QLSP/ADD/AddVC";
        }
        try {
            Integer soLuong = Integer.parseInt(voucher.getSoLuong());
            if (soLuong > 1000000) {
                model.addAttribute("errorsl", "Số lượng không được quá 1.000.000");
                return "admin/QLSP/ADD/AddVC";
            }
        } catch (Exception e) {
            model.addAttribute("errorsl", "Số lượng không hợp lệ");
            return "admin/QLSP/ADD/AddVC";
        }
        if (Integer.parseInt(voucher.getSoLuong()) < 1) {
            model.addAttribute("errorsl", "Số lượng phải lớn hơn hoặc bằng 1");
            return "admin/QLSP/ADD/AddVC";
        }
//        //điều kiện
        if (voucher.getDieuKien().isEmpty()) {
            model.addAttribute("errordk", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        }
        try {
            Integer dieuKien = Integer.parseInt(voucher.getDieuKien());
            if (dieuKien < 10000) {
                model.addAttribute("errordk", "Điều kiện phải lớn hơn hoặc bằng 10.000 VND");
                return "admin/QLSP/ADD/AddVC";
            }
        } catch (Exception e) {
            model.addAttribute("errordk", "Điều kiện không hợp lệ");
            return "admin/QLSP/ADD/AddVC";
        }
        if (!voucher.getDieuKien().matches("\\d+")) {
            model.addAttribute("errordk", "Diều kiện chỉ chứa số");
            return "admin/QLSP/ADD/AddVC";
        }

        //mức giảm
        if (voucher.getMucGiam() == null) {
            model.addAttribute("errormg", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.getMucGiam() == null || voucher.getMucGiam().length() >= 29) {
            model.addAttribute("errormg", "Mức giảm phải nhỏ hơn 29 ký tự");
            return "admin/QLSP/ADD/AddVC";
        }
        if (voucher.isLoai()) {
            // Kiểm tra mức giảm theo phần trăm (boolean = true)
            try {
                Long mucGiam = Long.parseLong(voucher.getMucGiam());
                if (mucGiam < 5 || mucGiam > 99) {
                    model.addAttribute("errormg", "Mức giảm theo % phải nằm trong khoảng từ 5% đến 99%");
                    return "admin/QLSP/ADD/AddVC";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errormg", "Mức giảm không hợp lệ");
                return "admin/QLSP/ADD/AddVC";
            }
        } else {
            // Kiểm tra mức giảm theo số tiền cụ thể (boolean = false)
            try {
                Long mucGiam = Long.parseLong(voucher.getMucGiam());
                if (mucGiam < 5000) {
                    model.addAttribute("errormg", "Mức giảm theo VND phải từ 5.000 trở lên");
                    return "admin/QLSP/ADD/AddVC";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errormg", "Mức giảm không hợp lệ");
                return "admin/QLSP/ADD/AddVC";
            }
        }
        if (!voucher.getMucGiam().matches("\\d+")) {
            model.addAttribute("errormg", "Mức giảm chỉ chứa số");
            return "admin/QLSP/ADD/AddVC";
        }

        if (voucher.getMota().isEmpty()) {
            model.addAttribute("errormt", "Không được để trống");
            return "admin/QLSP/ADD/AddVC";
        } else if (!motavcMatcher.matches()) {
            model.addAttribute("errormt", "Mô tả chỉ được chứa chữ và số");
            return "admin/QLSP/ADD/AddVC";
        }
        voucher.setNgaytao(LocalDate.now());
        voucherRepository.save(voucher);
        redirectAttributes.addFlashAttribute("Add", "Thêm voucher thành công");
        return "redirect:/panda/voucher/hienthi";
    }

    @GetMapping("/update/{id}")
    public String detailPB(@PathVariable Integer id, Model model) {

        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        model.addAttribute("Voucher", new Voucher());
        model.addAttribute("Voucher", voucherRepository.getReferenceById(id));

        return "/admin/QLSP/UPDATE/UpdateVC";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute("Voucher") Voucher voucher, RedirectAttributes
            redirectAttributes, @RequestParam("id") Integer id) {

        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        LocalDate today = LocalDate.now();
        String regexma = "^[a-zA-Z0-9]+$";
        String regex = "^[\\p{L}0-9\\s]+$";

        Pattern patternma = Pattern.compile(regexma);
        Pattern pattern = Pattern.compile(regex);

        Matcher mavcMatcher = patternma.matcher(voucher.getMa());
        Matcher tenvcMatcher = pattern.matcher(voucher.getTen());
        Matcher motavcMatcher = pattern.matcher(voucher.getMota());

        String ten = voucher.getTen().trim().toLowerCase();
        String ma = voucher.getMa().trim().toLowerCase();


        Voucher findma = voucherRepository.findByMaAndIdNot(ma, voucher.getId());
        Voucher findten = voucherRepository.findByTenAndIdNot(ten, voucher.getId());


        // mã voucher
        if (voucher.getMa().isEmpty()) {
            model.addAttribute("errorma", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (!mavcMatcher.matches()) {
            model.addAttribute("errorma", "Mã chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.getMa() == null || voucher.getMa().length() < 5 || voucher.getMa().length() > 14) {
            model.addAttribute("errorma", "Mã phải lớn hơn 4 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (findma != null) {
            model.addAttribute("errorma", "Mã đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        //tên voucher
        if (voucher.getTen().isEmpty() || voucher.getMa().isEmpty()) {
            model.addAttribute("errorten", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (!tenvcMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.getTen() == null || voucher.getTen().length() <= 1 || voucher.getTen().length() >= 30) {
            model.addAttribute("errorten", "Tên phải lớn hơn 1 ký tự và nhỏ hơn 30 ký tự");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (findten != null) {
            model.addAttribute("errorten", "Tên đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        //ngày bắt đầu
        if (voucher.getNgaybatdau() == null) {
            model.addAttribute("errornbd", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (today.isBefore(voucher.getNgaybatdau())) {
            // Nếu ngày hiện tại bé hơn ngày bắt đầu, trạng thái là "Sắp hoạt động"
            voucher.setTrangThai(0);
            model.addAttribute("warning", "Chú ý: Voucher chưa thể hoạt động vì ngày bắt đầu chưa đến, sẽ tự động bắt đầu hoạt động khi đến ngày!");

        }
        //ngày kết thúc
        if (voucher.getNgayketthuc() == null) {
            model.addAttribute("errornkt", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.getNgayketthuc().isBefore(voucher.getNgaybatdau()) || voucher.getNgayketthuc().isEqual(voucher.getNgaybatdau())) {
            model.addAttribute("errornkt", "Ngày kết thúc phải lớn hơn ngày bắt đầu");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.getNgayketthuc().isBefore(today)) {
            voucher.setTrangThai(2); // Đặt trạng thái thành "Ngừng hoạt động"
            model.addAttribute("errornkt", "Voucher không thể tạo vì đã qua ngày kết thúc");
            return "admin/QLSP/UPDATE/UpdateVC"; // Trả về trang với thông báo lỗi
        }
        //số lượng
        if (voucher.getSoLuong().isEmpty()) {
            model.addAttribute("errorsl", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.getSoLuong() == null || voucher.getSoLuong().length() >= 30) {
            model.addAttribute("errorsl", "Số lượng phải lớn hơn 0 ký tự và nhỏ hơn 30 ký tự");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        try {
            Integer soLuong = Integer.parseInt(voucher.getSoLuong());
            if (soLuong > 1000000) {
                model.addAttribute("errorsl", "Số lượng không được quá 1.000.000");
                return "admin/QLSP/UPDATE/UpdateVC";
            }
        } catch (Exception e) {
            model.addAttribute("errorsl", "Số lượng không hợp lệ");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (Integer.parseInt(voucher.getSoLuong()) < 1) {
            model.addAttribute("errorsl", "Số lượng phải lớn hơn hoặc bằng 1");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
//        //điều kiện
        if (voucher.getDieuKien().isEmpty()) {
            model.addAttribute("errordk", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        try {
            Integer dieuKien = Integer.parseInt(voucher.getDieuKien());
            if (dieuKien < 10000) {
                model.addAttribute("errordk", "Điều kiện phải lớn hơn hoặc bằng 10.000 VND");
                return "admin/QLSP/UPDATE/UpdateVC";
            }
        } catch (Exception e) {
            model.addAttribute("errordk", "Điều kiện không hợp lệ");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (!voucher.getDieuKien().matches("\\d+")) {
            model.addAttribute("errordk", "Diều kiện chỉ chứa số");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        //mức giảm
        if (voucher.getMucGiam() == null) {
            model.addAttribute("errormg", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.getMucGiam() == null || voucher.getMucGiam().length() >= 29) {
            model.addAttribute("errormg", "Mức giảm phải nhỏ hơn 29 ký tự");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        if (voucher.isLoai()) {
            // Kiểm tra mức giảm theo phần trăm (boolean = true)
            try {
                Long mucGiam = Long.parseLong(voucher.getMucGiam());
                if (mucGiam < 5 || mucGiam > 99) {
                    model.addAttribute("errormg", "Mức giảm theo % phải nằm trong khoảng từ 5% đến 99%");
                    return "admin/QLSP/UPDATE/UpdateVC";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errormg", "Mức giảm không hợp lệ");
                return "admin/QLSP/UPDATE/UpdateVC";
            }
        } else {
            // Kiểm tra mức giảm theo số tiền cụ thể (boolean = false)
            try {
                Long mucGiam = Long.parseLong(voucher.getMucGiam());
                if (mucGiam < 5000) {
                    model.addAttribute("errormg", "Mức giảm theo VND phải từ 5.000 trở lên");
                    return "admin/QLSP/UPDATE/UpdateVC";
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errormg", "Mức giảm không hợp lệ");
                return "admin/QLSP/UPDATE/UpdateVC";
            }
        }
        if (!voucher.getMucGiam().matches("\\d+")) {
            model.addAttribute("errormg", "Mức giảm chỉ chứa số");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        //mô tả
        if (voucher.getMota().isEmpty()) {
            model.addAttribute("errormt", "Không được để trống");
            return "admin/QLSP/UPDATE/UpdateVC";
        } else if (!motavcMatcher.matches()) {
            model.addAttribute("errormt", "Mô tả chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateVC";
        }
        List<Voucher> listvc = voucherRepository.findAll();
        LocalDate ngayTao = null;
        for (Voucher vc : listvc) {
            if (vc.getId() == id) {
                ngayTao = vc.getNgaytao();
            }
        }
        System.out.println(voucher.isLoaikhachhang());
        voucher.setNgaytao(ngayTao);
        voucher.setNgaysua(LocalDate.now());
        voucherRepository.save(voucher);
        redirectAttributes.addFlashAttribute("Update", "Sửa thành công!");
        return "redirect:/panda/voucher/hienthi";
    }

        @ResponseBody
        @GetMapping("/active-public")
        public List<Voucher> getActivePublicVouchers() {
            return voucherService.getActivePublicVouchers();
        }

}
