package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.ChatLieu;
import com.example.demo.respository.DanhMucRepository;
import com.example.demo.entity.DanhMuc;
import com.example.demo.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/panda/danhmuc")
public class DanhMucController {

    @Autowired
    DanhMucRepository danhMucRepository;

    @Autowired
    DanhMucService danhMucService;
    DanhMuc danhMuc = new DanhMuc();

    @GetMapping()
    public String danhmuc(@RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "tendm", required = false) String tendm,
                          @RequestParam(value = "trangthai", required = false) Integer trangthai,
                          Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        if (page < 0) {
            page = 0;
        }
        Page<DanhMuc> listDM = danhMucService.hienThiDM(page, tendm, trangthai);
        model.addAttribute("lsdanhmuc", listDM.getContent());
        model.addAttribute("totalPage", listDM.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("tendm", tendm);
        model.addAttribute("trangthai", trangthai);
        model.addAttribute("pageSize", listDM.getSize());
        return "admin/QLSP/DanhMuc";
    }

    @GetMapping("add")
    public String formadd(Model model) {
        String role = "admin";
        model.addAttribute("role", role);
        model.addAttribute( "danhmuc", danhMuc);
        return "admin/QLSP/ADD/AddDanhMuc";
    }

    @GetMapping("update")
    public String formupdate(@RequestParam("id") int id, Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute( "danhmuc", danhMucRepository.findById(id));
        return "admin/QLSP/UPDATE/UpdateDanhMuc";
    }

    @GetMapping("change")
    public String change(@RequestParam("id") int id, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);
        DanhMuc dm = danhMucRepository.findById(id).orElse(null);
        if (dm != null) {
            if (dm.getTrangthai() == 1) {
                dm.setTrangthai(0);
            } else {
                dm.setTrangthai(1);
            }
            danhMucRepository.save(dm);
            redirectAttributes.addFlashAttribute("thongbao","Thành công !");
        }
        return "redirect:/panda/danhmuc";
    }


    @PostMapping("add")
    public String add(@ModelAttribute("danhmuc") DanhMuc danhMuc , Model model,RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);
        model.addAttribute( "nsx", danhMuc);
        String regex = "^[a-zA-Z0-9àáạảãâầấậẩăằắặẳêềếệểèẻẹéẽôồốộổơờớợởưừứựửữủũụúùìỉịíĩđòóỏọõ\\s]+$";
        String regexma = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Pattern patternma = Pattern.compile(regexma);

        Matcher tennsxMatcher = pattern.matcher(danhMuc.getTendanhmuc());
        Matcher mansxMatcher = patternma.matcher(danhMuc.getMadanhmuc());
        boolean loi = true ;
        if(danhMuc.getMadanhmuc().isEmpty()){
            model.addAttribute("errorma","Không được để trống");
            loi = false;
        }else if (!mansxMatcher.matches()) {
            model.addAttribute("errorma" ,"Mã chỉ được chứa chữ và số");
            loi = false;
        }
        if(danhMucRepository.existsDanhMucByMadanhmuc(danhMuc.getMadanhmuc())){
            model.addAttribute("errorma","Mã đã tồn tại");
            loi = false;
        }

        if(danhMuc.getTendanhmuc().isEmpty() || danhMuc.getMadanhmuc().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            loi = false;
        }else if (!tennsxMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            loi = false;
        }
        if(danhMucRepository.existsDanhMucByTendanhmuc(danhMuc.getTendanhmuc())){
            model.addAttribute("errorten","Tên đã tồn tại");
            loi = false;
        }
        if(loi == false) {
            model.addAttribute("thongbao","Thất bại!");
            return "admin/QLSP/ADD/AddDanhMuc";
        }else{
            redirectAttributes.addFlashAttribute("thongbao","Thêm thành công!");
            danhMucRepository.save(danhMuc);
            return "redirect:/panda/danhmuc";
        }
    }

    @PostMapping("update")
    public String update(@ModelAttribute("danhmuc") DanhMuc danhMuc , Model model,RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);
        model.addAttribute( "nsx", danhMuc);

        String ten = danhMuc.getTendanhmuc().trim().toLowerCase();
        String ma = danhMuc.getMadanhmuc().trim().toLowerCase();

        String regex = "^[a-zA-Z0-9àáạảãâầấậẩăằắặẳêềếệểèẻẹéẽôồốộổơờớợởưừứựửữủũụúùìỉịíĩđòóỏọõ\\s]+$";
        String regexma = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Pattern patternma = Pattern.compile(regexma);

        String tendm = danhMuc.getTendanhmuc().trim().toLowerCase();
        Matcher mansxMatcher = patternma.matcher(danhMuc.getMadanhmuc());
        Matcher tennsxMatcher = pattern.matcher(tendm);

        DanhMuc findma = danhMucRepository.findByMadanhmucAndIdNot(ma,danhMuc.getId());
        DanhMuc findten = danhMucRepository.findByTendanhmucAndIdNot(ten,danhMuc.getId());
        boolean loi = true ;
        if(danhMuc.getMadanhmuc().isEmpty()){
            model.addAttribute("errorma","Không được để trống");
            loi = false;
        }else if (!mansxMatcher.matches()) {
            model.addAttribute("errorma" ,"Mã chỉ được chứa chữ và số");
            loi = false;
        }
        if(findma != null){
            model.addAttribute("errorma","Mã đã tồn tại");
            loi = false;
        }
        if(danhMuc.getTendanhmuc().isEmpty() || danhMuc.getMadanhmuc().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            loi = false;
        }else if (!tennsxMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            loi = false;
        }
        if(findten != null){
            model.addAttribute("errorten","Tên đã tồn tại");
            loi = false;
        }
        if(loi == false) {
            model.addAttribute("thongbao","Thất bại!");
            return "admin/QLSP/UPDATE/UpdateDanhMuc";
        }else{
            redirectAttributes.addFlashAttribute("thongbao","Sửa thành công!");
            danhMuc.setNgaysua(LocalDate.now());
            danhMucRepository.save(danhMuc);
            return "redirect:/panda/danhmuc";
        }


    }

}
