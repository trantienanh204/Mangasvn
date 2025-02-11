package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.CoAo;
import com.example.demo.respository.CoAoRepository;
import com.example.demo.service.CoAoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/panda/coao")
public class CoAoController {
    @Autowired
    CoAoRepository coAoRepository;
    @Autowired
    CoAoService coAoService;
    @GetMapping("/hienthi")
    public String index( @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "tenca", required = false) String tenca,
                         @RequestParam(value = "trangthai", required = false) Integer trangthai,
                         Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        if (page < 0) {
            page = 0;
        }
        Page<CoAo> listCA = coAoService.hienThiCA(page, tenca, trangthai);
        model.addAttribute("totalPage", listCA.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("tenca", tenca);
        model.addAttribute("trangthai", trangthai);
        model.addAttribute("list", listCA.getContent());
        model.addAttribute("CoAo", new CoAo());
        model.addAttribute("pageSize", listCA.getSize());
        return "admin/QLSP/CoAo";
    }

    @GetMapping("/add")
    public String showFormAdd(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("coAo", new CoAo());
        CoAo coAo = new CoAo();
        coAo.setTrangThai(0); // Giá trị mặc định là 0 (Hoạt động)
        model.addAttribute("coAo", coAo);
        return "admin/QLSP/ADD/addCoAo";
    }

    @PostMapping("/add")
    public String add(Model model, @Valid @ModelAttribute CoAo coAo, BindingResult result, RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);

        // Kiểm tra lỗi trong binding
        if (result.hasErrors()) {
            return "admin/QLSP/ADD/AddCoAo"; // Trả về trang thêm mới nếu có lỗi
        }

        // Kiểm tra mã đã tồn tại
        if (coAoRepository.existsCoAoByMa(coAo.getMa())) {
            model.addAttribute("errorma", "Mã đã tồn tại");
            return "admin/QLSP/ADD/AddCoAo";
        }

        // Kiểm tra tên đã tồn tại
        if (coAoRepository.existsCoAoByTen(coAo.getTen())) {
            model.addAttribute("errorten", "Tên đã tồn tại");
            return "admin/QLSP/ADD/AddCoAo";
        }

        // Nếu ID không phải là null, thực hiện cập nhật
        if (coAo.getId() != null) {
            CoAo existingCoAo = coAoRepository.findById(coAo.getId()).orElse(null);
            if (existingCoAo != null) {
                existingCoAo.setMa(coAo.getMa());
                existingCoAo.setTen(coAo.getTen());
                existingCoAo.setTrangThai(coAo.getTrangThai());
                existingCoAo.setNgaySua(LocalDateTime.now());
                coAoRepository.save(existingCoAo);
            } else {
                redirectAttributes.addFlashAttribute("ErrorStatusMessage", "Thêm không thành công!");
                return "redirect:/panda/coao/hienthi"; // Redirect sau khi có lỗi
            }
        } else {
            // Thêm mới
            coAo.setNgayTao(LocalDateTime.now());
            coAo.setNgaySua(LocalDateTime.now());
            coAoRepository.save(coAo);
        }

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("AddStatusMessage", "Thêm thành công !");
        return "redirect:/panda/coao/hienthi";
    }

    @GetMapping("/update")
    public String showFormUpdate(Model model, @RequestParam("id") Integer id) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("coAo", new CoAo());
        model.addAttribute("coAo", coAoRepository.findById(id).get());
        return "admin/QLSP/UPDATE/UpdateCoAo";
    }

    @PostMapping("/update")
    public String update(@Validated @ModelAttribute CoAo coAo, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);
        if (bindingResult.hasErrors()) {
            return "admin/QLSP/UPDATE/UpdateCoAo";
        }

        // Nếu ID không phải là null, thực hiện cập nhật
        if (coAo.getId() != null) {
            CoAo existingCoAo = coAoRepository.findById(coAo.getId()).orElse(null);
            if (existingCoAo != null) {
                existingCoAo.setMa(coAo.getMa());
                existingCoAo.setTen(coAo.getTen());
                existingCoAo.setTrangThai(coAo.getTrangThai());
                existingCoAo.setNgaySua(LocalDateTime.now());
                coAoRepository.save(existingCoAo);
            } else {
                redirectAttributes.addFlashAttribute("ErrorStatusMessage", "Thêm không thành công!");
                return "redirect:/panda/coao/hienthi"; // Redirect sau khi có lỗi
            }
        } else {
            // Thêm mới
            coAo.setNgayTao(LocalDateTime.now());
            coAo.setNgaySua(LocalDateTime.now());
            coAoRepository.save(coAo);
        }
        redirectAttributes.addFlashAttribute("UpdateStatusMessage", "Cập nhật thành công !");
        return "redirect:/panda/coao/hienthi";
    }
    @GetMapping("/change")
    public String changeStatus(@RequestParam("id") int id, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin"; // Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        CoAo coAo = coAoRepository.findById(id).orElse(null);
        if (coAo != null) {
            coAo.setTrangThai(coAo.getTrangThai() == 1 ? 0 : 1); // Đảo ngược trạng thái
            coAoRepository.save(coAo);
        }
        redirectAttributes.addFlashAttribute("ChangesStatusMessage", "Chuyển trạng thái thành công !");
        return "redirect:/panda/coao/hienthi";
    }
}
