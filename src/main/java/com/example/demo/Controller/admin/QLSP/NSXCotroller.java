package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.DanhMuc;
import com.example.demo.respository.NSXRepository;
import com.example.demo.entity.NhaSanXuat;
import com.example.demo.service.NSXService;
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
@RequestMapping("/panda/nsx")
public class NSXCotroller {
    @Autowired
    NSXRepository nsxRepository;
    @Autowired
    NSXService nsxService;
    NhaSanXuat nhaSanXuat = new NhaSanXuat();


    @GetMapping()
    public String nsx(@RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "tennsx", required = false) String tennsx,
                      @RequestParam(value = "trangthai", required = false) Integer trangthai,
                      Model model) {
        String role = "admin";
        model.addAttribute("role", role);
        if (page < 0) {
            page = 0;
        }
        Page<NhaSanXuat> listNSX = nsxService.hienThiNSX(page, tennsx, trangthai);
        model.addAttribute("lsnsx",listNSX.getContent());
        model.addAttribute("totalPage", listNSX.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("tennsx", tennsx);
        model.addAttribute("trangthai", trangthai);
        model.addAttribute("pageSize", listNSX.getSize());
        return "admin/QLSP/NSX";
    }

    @GetMapping("add")
    public String formadd(Model model,RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute( "nsx", nhaSanXuat);

        return "admin/QLSP/ADD/AddNsx";
    }

    @GetMapping("update")
    public String formupdate(@RequestParam("id") int id, Model model,RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute( "nsx", nsxRepository.findById(id));
        return "admin/QLSP/UPDATE/UpdateNsx";
    }

    @GetMapping("change")
    public String delete(@RequestParam("id") int id, Model model,RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        NhaSanXuat nsx = nsxRepository.findById(id).orElse(null);
        if (nsx != null) {
            if (nsx.getTrangthai() == 1) {
                nsx.setTrangthai(0);
            } else {
                nsx.setTrangthai(1);
            }
            nsxRepository.save(nsx);
            redirectAttributes.addFlashAttribute("thongbao","Thành công !");
        }
        //redirectAttributes.addFlashAttribute("thongbao","Thanh cong !");
        return "redirect:/panda/nsx";
    }


    @PostMapping("add")
    public String add( @ModelAttribute("nsx") NhaSanXuat nhaSanXuat , Model model,RedirectAttributes redirectAttributes) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute( "nsx", nhaSanXuat);

        String regex = "^[\\p{L}0-9\\s]+$";
        Pattern pattern = Pattern.compile(regex);

        String regexma = "^[a-zA-Z0-9]+$";
        Pattern patternma = Pattern.compile(regexma);

        String ten = nhaSanXuat.getTennsx().toLowerCase().trim();
        Matcher mansxMatcher = patternma.matcher(nhaSanXuat.getMansx());
        Matcher tennsxMatcher = pattern.matcher(ten);
        boolean loi = true ;
        if(nhaSanXuat.getMansx().isEmpty()){
            model.addAttribute("errorma","Không được để trống");

        }else if (!mansxMatcher.matches()) {
            loi = false;
            model.addAttribute("errorma" ,"Mã chỉ được chứa chữ và số");
        }
        if(nsxRepository.existsNhaSanXuatByMansx(nhaSanXuat.getMansx())){
            loi = false;
            model.addAttribute("errorma" ,"Mã đã tồn tại");
        }
        if(nhaSanXuat.getTennsx().isEmpty() || nhaSanXuat.getMansx().isEmpty()){
            loi = false;
            model.addAttribute("errorten","Không được để trống");
        }else if (!tennsxMatcher.matches()) {
            loi = false;
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
        }
        if(nsxRepository.existsNhaSanXuatByTennsx(nhaSanXuat.getTennsx())){
             loi = false;
             model.addAttribute("errorten","Tên đã tồn tại");
         }
        if(loi == false){
            model.addAttribute("thongbao","Thất bại!");
            return "admin/QLSP/ADD/AddNsx";
        }else{
            nsxRepository.save(nhaSanXuat);
            redirectAttributes.addFlashAttribute("thongbao","Thêm thành công!");
            return "redirect:/panda/nsx";
        }

    }

    @PostMapping("update")
    public String update(@ModelAttribute("nsx") NhaSanXuat nhaSanXuat , Model model,RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);
        model.addAttribute( "nsx", nhaSanXuat);

        String tennsx = nhaSanXuat.getTennsx().trim().toLowerCase();
        String mansx = nhaSanXuat.getMansx().trim().toLowerCase();
        NhaSanXuat findma = nsxRepository.findByMansxAndIdNot(mansx,nhaSanXuat.getId());
        NhaSanXuat findten = nsxRepository.findByTennsxAndIdNot(tennsx,nhaSanXuat.getId());
        String regex = "^[a-zA-Z0-9àáạảãâầấậẩăằắặẳêềếệểèẻẹéẽôồốộổơờớợởưừứựửữủũụúùìỉịíĩđòóỏọõ\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        String regexma = "^[a-zA-Z0-9]+$";

        Pattern patternma = Pattern.compile(regexma);

        String ten = nhaSanXuat.getTennsx().toLowerCase().trim();
        Matcher mansxMatcher = patternma.matcher(nhaSanXuat.getMansx());
        Matcher tennsxMatcher = pattern.matcher(ten);

        if(nhaSanXuat.getMansx().isEmpty()){
            model.addAttribute("errorma","Không được để trống");
            return "admin/QLSP/UPDATE/UpdateNsx";
        }else if (!mansxMatcher.matches()) {
            model.addAttribute("errorma" ,"Mã chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateNsx";
        }
        if(findma !=null){
            model.addAttribute("errorma","Mã đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateNsx";
        }

        if(nhaSanXuat.getTennsx().isEmpty() || nhaSanXuat.getMansx().isEmpty()){
            model.addAttribute("errorten","Không được để trống");
            return "admin/QLSP/UPDATE/UpdateNsx";
        }else if (!tennsxMatcher.matches()) {
            model.addAttribute("errorten", "Tên chỉ được chứa chữ và số");
            return "admin/QLSP/UPDATE/UpdateNsx";
        }
        if(findten !=null){
            model.addAttribute("errorten","Tên đã tồn tại");
            return "admin/QLSP/UPDATE/UpdateNsx";
        }
        nhaSanXuat.setNgaysua(LocalDate.now());
        redirectAttributes.addFlashAttribute("thongbao","Sửa thành công !");
        nsxRepository.save(nhaSanXuat);
        return "redirect:/panda/nsx";
    }
}
