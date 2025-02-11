package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.KichThuoc;
import com.example.demo.respository.KichThuocRepository;
import com.example.demo.service.KichThuocService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/panda/kichthuoc")
public class kichthuocController {
    @Autowired
    KichThuocRepository kichThuocRepository;
    @Autowired
    KichThuocService kichThuocService;

    @GetMapping("/hienthi")
    public String viewKT(@RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(value = "tenkt", required = false) String tenkt,
                         @RequestParam(value = "trangthai", required = false) Integer trangthai,
                         Model model) {
        //slide bar
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        if (page < 0) {
            page = 0;
        }
        Page<KichThuoc> listKT = kichThuocService.hienThiKT(page, tenkt, trangthai);
        model.addAttribute("listkt", listKT.getContent());
        model.addAttribute("totalPage", listKT.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("tenkt", tenkt);
        model.addAttribute("trangthai", trangthai);
        model.addAttribute("KichThuoc", new KichThuoc());
        model.addAttribute("pageSize", listKT.getSize());
        return "/admin/QLSP/KichThuoc";
    }

    @PostMapping("/add")
    public String AddVC( @ModelAttribute("KichThuoc") KichThuoc kichThuoc, Model model,RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        String regex = "^[\\p{L}0-9\\s]+$";
        String regexma = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Pattern patternma = Pattern.compile(regexma);

        Matcher maktMatcher = patternma.matcher(kichThuoc.getMa());
        Matcher tenktMatcher = pattern.matcher(kichThuoc.getTen());


        if(kichThuoc.getMa().isEmpty()){
            model.addAttribute("errorma","Không được để trống");
            return "admin/QLSP/ADD/AddKT";
        }
        if (kichThuoc.getMa() == null || kichThuoc.getMa().length() < 3 || kichThuoc.getMa().length() > 14) {
            model.addAttribute("errorma", "Mã phải lớn hơn hoặc bằng 3 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/ADD/AddKT";
        }
        if (!maktMatcher.matches()) {
            model.addAttribute("errorma" ,"Mã chỉ được chứa chữ và số");
            return "admin/QLSP/ADD/AddKT";
        }
        if(kichThuocRepository.existsKichThuocByMa(kichThuoc.getMa())){
            model.addAttribute("errorma","Mã đã tồn tại");
            return "admin/QLSP/ADD/AddKT";
        }
        if(kichThuoc.getTen().isEmpty() || kichThuoc.getMa().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            return "admin/QLSP/ADD/AddKT";
        }else if (!tenktMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/ADD/AddKT";
        }
        if (kichThuoc.getTen() == null || kichThuoc.getTen().length() < 1 || kichThuoc.getTen().length() > 14) {
            model.addAttribute("errorten", "Tên phải lớn hơn hoặc bằng 1 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/ADD/AddKT";
        }
        if(kichThuocRepository.existsKichThuocByTen(kichThuoc.getTen())){
            model.addAttribute("errorten","Tên đã tồn tại");
            return "admin/QLSP/ADD/AddKT";
        }
        if (kichThuoc.getTrangthai() == null) {
            model.addAttribute("errortt", "Trạng thái không được để trống");
            return "admin/QLSP/ADD/AddKT";
        }
        kichThuoc.setNgaytao(LocalDate.now());
        kichThuocRepository.save(kichThuoc);
        redirectAttributes.addFlashAttribute("Add", "Thêm thành công!");

        return "redirect:/panda/kichthuoc/hienthi";
    }

    @GetMapping("/viewadd")
    public String view(Model model) {
        model.addAttribute("KichThuoc", new KichThuoc());
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        KichThuoc kichThuoc = new KichThuoc();
        kichThuoc.setTrangthai(0); // Giá trị mặc định là 0 (Hoạt động)
        model.addAttribute("KichThuoc",kichThuoc);
        return "/admin/QLSP/ADD/AddKT";
    }

    @GetMapping("/remove/{id}")
    public String removePB(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        KichThuoc kichThuoc = kichThuocRepository.findById(id).orElse(null);
        if (kichThuoc != null) {
            kichThuoc.setTrangthai(kichThuoc.getTrangthai() == 1 ? 0 : 1);
            kichThuocRepository.save(kichThuoc);
            redirectAttributes.addFlashAttribute("UpdateStatusMessage", "Chuyển trạng thái thành công!");
        }
        return "redirect:/panda/kichthuoc/hienthi";
    }

    @GetMapping("/update/{id}")
    public String detailPB(@PathVariable Integer id, Model model) {
        model.addAttribute("KichThuoc", new KichThuoc());
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("KichThuoc", kichThuocRepository.getReferenceById(id));
        return "/admin/QLSP/Update/UpdateKT";
    }
    @PostMapping("/update")
    public String update(Model model, @ModelAttribute("KichThuoc") KichThuoc kichThuoc, RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        String ten = kichThuoc.getTen().trim().toLowerCase();
        String ma = kichThuoc.getMa().trim().toLowerCase();

        String regex = "^[\\p{L}0-9\\s]+$";
        String regexma = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Pattern patternma = Pattern.compile(regexma);

        Matcher maktMatcher = patternma.matcher(kichThuoc.getMa());
        Matcher tenktMatcher = pattern.matcher(kichThuoc.getTen());

        KichThuoc findma = kichThuocRepository.findByMaAndIdNot(ma,kichThuoc.getId());
        KichThuoc findten = kichThuocRepository.findByTenAndIdNot(ten,kichThuoc.getId());

        if(kichThuoc.getMa().isEmpty()){
            model.addAttribute("errorma","Không được để trống");
            return "admin/QLSP/UPDATE/UpdateKT";
        }else if (!maktMatcher.matches()) {
            model.addAttribute("errorma" ,"Mã chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateKT";
        }
        if (kichThuoc.getMa() == null || kichThuoc.getMa().length() < 3 || kichThuoc.getMa().length() > 14) {
            model.addAttribute("errorma", "Mã phải lớn hơn hoặc bằng 3 ký tự và nhỏ hơn 15 ký tự");
            return "admin/QLSP/UPDATE/UpdateKT";
        }
        if(findma != null){
            model.addAttribute("errorma","Mã đã tồn tại");
         return "admin/QLSP/UPDATE/UpdateKT";
        }
        if(kichThuoc.getTen().isEmpty() || kichThuoc.getMa().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            return "admin/QLSP/UPDATE/UpdateKT";
        }else if (!tenktMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateKT";
        }

        if (kichThuoc.getTen() == null || kichThuoc.getTen().length() < 1 || kichThuoc.getTen().length() > 14) {
            model.addAttribute("errorten", "Tên phải lớn hơn hoặc bằng 1 ký tự và nhỏ hơn 15 ký tự");

            return "admin/QLSP/UPDATE/UpdateKT";
        }
        if(findten != null){
            model.addAttribute("errorten","Tên đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateKT";
        }
        if (kichThuoc.getTrangthai() == null) {
            model.addAttribute("errortt", "Trạng thái không được để trống");
            return "admin/QLSP/UPDATE/UpdateKT";
        }
        kichThuoc.setNgaysua(LocalDate.now());
        kichThuocRepository.save(kichThuoc);
        redirectAttributes.addFlashAttribute("Update", "Sửa thành công!");
        return "redirect:/panda/kichthuoc/hienthi";
    }
}
