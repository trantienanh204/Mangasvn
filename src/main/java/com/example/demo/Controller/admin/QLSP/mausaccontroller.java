package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.MauSac;
import com.example.demo.entity.SanPham;
import com.example.demo.respository.MauSacRepsitory;
import com.example.demo.service.MauSacService;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
@RequestMapping("/panda/mausac")
public class mausaccontroller {
    @Autowired
    MauSacRepsitory mauSacRepsitory;
    @Autowired
    MauSacService mauSacService;
    @GetMapping("/hienthi")
    public String viewVC(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "tenms", required = false) String tenms,
            @RequestParam(value = "trangthai", required = false) Integer trangthai,
            Model model) {
        String role = "admin";
        model.addAttribute("role", role);
        if (page < 0) {
            page = 0;
        }
        Page<MauSac> listMS = mauSacService.hienThimausac(page, tenms, trangthai);
        model.addAttribute("listms", listMS.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", listMS.getTotalPages());
        model.addAttribute("tenms", tenms);
        model.addAttribute("trangthai", trangthai);
        model.addAttribute("MauSac", new MauSac());
        model.addAttribute("pageSize", listMS.getSize());

        return "/admin/QLSP/MauSac";
    }

    @PostMapping("/add")
    public String AddVC(@ModelAttribute("MauSac") MauSac mauSac, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin"; // Lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        String regex = "^[\\p{L}0-9\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher tenmsMatcher = pattern.matcher(mauSac.getTen());

        if (mauSac.getMa().isEmpty()) {
            model.addAttribute("errorma", "Mã màu không được để trống");
            return "admin/QLSP/ADD/AddMS";
        }
        if (mauSac.getMa() == null || mauSac.getMa().length() < 5 || mauSac.getMa().length() > 14) {
            model.addAttribute("errorma", "Mã phải lớn hơn 4 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/ADD/AddMS";
        }
        if (mauSacRepsitory.existsMauSacByMa(mauSac.getMa())) {
            model.addAttribute("errorma", "Mã màu đã tồn tại");
            return "admin/QLSP/ADD/AddMS";
        }
        if(mauSac.getTen().isEmpty() || mauSac.getMa().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            return "admin/QLSP/ADD/AddMS";
        }if (!tenmsMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/ADD/AddMS";
        }
        if (mauSac.getTen() == null || mauSac.getTen().length() < 2 || mauSac.getTen().length() > 14) {
            model.addAttribute("errorten", "Tên phải lớn hơn 1 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/ADD/AddMS";
        }
        if (!Character.isLetter(mauSac.getTen().charAt(0))) {
            model.addAttribute("errorten", "Ký tự đầu tiên phải là chữ cái");
            return "admin/QLSP/ADD/AddMS";
        }
        if (mauSacRepsitory.existsMauSacByTen(mauSac.getTen())) {
            model.addAttribute("errorten", "Tên màu đã tồn tại");
            return "admin/QLSP/ADD/AddMS";
        }
        if (mauSac.getTrangthai() == null) {
            model.addAttribute("errortt", "Trạng thái không được để trống");
            return "admin/QLSP/ADD/AddMS";
        }
        mauSac.setNgaytao(LocalDate.now());
        mauSacRepsitory.save(mauSac);
        redirectAttributes.addFlashAttribute("Add", "Thêm màu sắc thành công");

        return "redirect:/panda/mausac/hienthi";
    }

    @GetMapping("/viewadd")
    public String view(Model model) {
        model.addAttribute("MauSac", new MauSac());
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        MauSac mauSac = new MauSac();
        mauSac.setTrangthai(0); // Giá trị mặc định là 0 (Hoạt động)
        model.addAttribute("MauSac",mauSac);
        return "/admin/QLSP/ADD/AddMS";
    }

    @GetMapping("/remove/{id}")
    public String removePB(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        MauSac mauSac = mauSacRepsitory.findById(id).orElse(null);
        if (mauSac != null) {
            mauSac.setTrangthai(mauSac.getTrangthai() == 1 ? 0 : 1);
            mauSacRepsitory.save(mauSac);
            redirectAttributes.addFlashAttribute("UpdateStatusMessage", "Chuyển trạng thái thành công!");
        }
        return "redirect:/panda/mausac/hienthi";
    }


    @GetMapping("/update/{id}")
    public String detailPB(@PathVariable Integer id, Model model) {
        model.addAttribute("MauSac", new MauSac());
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("MauSac", mauSacRepsitory.getReferenceById(id));
        return "/admin/QLSP/UPDATE/UpdateMS";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute("MauSac") MauSac mauSac,RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        String tenms = mauSac.getTen().trim().toLowerCase();
        String ma = mauSac.getMa().trim().toLowerCase();

        String regex = "^[\\p{L}0-9\\s]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher tenmsMatcher = pattern.matcher(tenms);

        MauSac findma = mauSacRepsitory.findByMaAndIdNot(ma,mauSac.getId());
        MauSac findten = mauSacRepsitory.findByTenAndIdNot(tenms,mauSac.getId());

        // Kiểm tra sự thay đổi
        if(mauSac.getMa().isEmpty()){
            model.addAttribute("errorma","Không được để trống");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if (mauSac.getMa() == null || mauSac.getMa().length() < 2 || mauSac.getMa().length() > 14) {
            model.addAttribute("errorma", "Mã phải lớn hơn 1 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if(findma != null){
            model.addAttribute("errorma","Mã đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if(mauSac.getTen().isEmpty() || mauSac.getMa().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            return "admin/QLSP/UPDATE/UpdateMS";
        }else if (!tenmsMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if (mauSac.getTen() == null || mauSac.getTen().length() < 2 || mauSac.getTen().length() > 14) {
            model.addAttribute("errorten", "Tên phải lớn hơn 1 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if (!Character.isLetter(mauSac.getTen().charAt(0))) {
            model.addAttribute("errorten", "Ký tự đầu tiên phải là chữ cái");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if(findten != null){
            model.addAttribute("errorten","Tên đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        if (mauSac.getTrangthai() == null) {
            model.addAttribute("errortt", "Trạng thái không được để trống");
            return "admin/QLSP/UPDATE/UpdateMS";
        }
        mauSac.setNgaysua(LocalDate.now());
        mauSacRepsitory.save(mauSac);
        redirectAttributes.addFlashAttribute("Update", "Sửa thành công!");
        return "redirect:/panda/mausac/hienthi";
    }

}