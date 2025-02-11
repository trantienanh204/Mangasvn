package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.KichThuoc;
import com.example.demo.respository.ChatLieuRespository;
import com.example.demo.service.ChatLieuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/panda/chatlieu")
public class ChatLieuController {

    @Autowired
    ChatLieuRespository chatLieuRespository;
    @Autowired
    ChatLieuService chatLieuService;
    @GetMapping("/hienthi")
    public String chatlieu( @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "tencl", required = false) String tencl,
                            @RequestParam(value = "trangthai", required = false) Integer trangthai,
                            Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        if (page < 0) {
            page = 0;
        }
        Page<ChatLieu> listCL = chatLieuService.hienThiCL(page, tencl, trangthai);
        model.addAttribute("list", listCL.getContent());
        model.addAttribute("totalPage", listCL.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("tencl", tencl);
        model.addAttribute("trangthai", trangthai);
        model.addAttribute("ChatLieu", new ChatLieu());
        model.addAttribute("pageSize", listCL.getSize());
        return "/admin/QLSP/ChatLieu";
    }

    @GetMapping("/add")
    public String index(Model model) {
        model.addAttribute("role", "admin");
        model.addAttribute("chatLieu", new ChatLieu());
        ChatLieu chatLieu = new ChatLieu();
        chatLieu.setTrangThai(0); // Giá trị mặc định là 0 (Hoạt động)
        model.addAttribute("chatLieu", chatLieu);
        return "admin/QLSP/ADD/AddChatLieu";
    }
    // add
    @PostMapping("/add")
    public String add(Model model, @Valid @ModelAttribute ChatLieu chatLieu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role", role);
        // Tìm kiếm chất liệu nếu có ID
        ChatLieu existingChatLieu = chatLieuRespository.findById(chatLieu.getId()).orElse(null);

        // Kiểm tra lỗi trong binding
        if (bindingResult.hasErrors()) {
            return "admin/QLSP/ADD/AddChatLieu";
        }

        // Kiểm tra mã chất liệu đã tồn tại
        if (chatLieuRespository.existsChatLieuByMaChatLieu(chatLieu.getMaChatLieu())) {
            model.addAttribute("errorma", "Mã chất liệu đã tồn tại");
            return "admin/QLSP/ADD/AddChatLieu";
        }

        // Kiểm tra tên chất liệu đã tồn tại
        if (chatLieuRespository.existsChatLieuByTenChatLieu(chatLieu.getTenChatLieu())) {
            model.addAttribute("errorten", "Tên chất liệu đã tồn tại");
            return "admin/QLSP/ADD/addChatLieu";
        }

        // Thêm mới hoặc cập nhật
        if (existingChatLieu != null) {
            // Cập nhật
            existingChatLieu.setMaChatLieu(chatLieu.getMaChatLieu());
            existingChatLieu.setTenChatLieu(chatLieu.getTenChatLieu());
            existingChatLieu.setTrangThai(chatLieu.getTrangThai());
            existingChatLieu.setNgaySua(LocalDateTime.now());
            chatLieuRespository.save(existingChatLieu);
        } else {
            // Thêm mới
            chatLieu.setNgayTao(LocalDateTime.now());
            chatLieu.setNgaySua(LocalDateTime.now());
            chatLieuRespository.save(chatLieu);
        }
        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("AddStatusMessage", "Thêm thành công !");
        return "redirect:/panda/chatlieu/hienthi";
    }


    @GetMapping("/update")
    public String showUpdate(@RequestParam("id") int id, Model model) {
        String role = "admin";
        model.addAttribute("role", role);
        model.addAttribute("chatLieu", new ChatLieu());
        model.addAttribute("chatLieu", chatLieuRespository.findById(id).get());
        return "admin/QLSP/UPDATE/UpdateChatLieu";
    }

    @PostMapping("/update")
    public String update(@Validated @ModelAttribute ChatLieu chatLieu, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin";
        model.addAttribute("role",role);
        ChatLieu existingChatLieu = chatLieuRespository.findById(chatLieu.getId()).orElse(null);
        if (bindingResult.hasErrors()) {
            return "admin/QLSP/UPDATE/UpdateChatLieu";
        }

        if (existingChatLieu != null) {
            existingChatLieu.setMaChatLieu(chatLieu.getMaChatLieu());
            existingChatLieu.setTenChatLieu(chatLieu.getTenChatLieu());
            existingChatLieu.setTrangThai(chatLieu.getTrangThai());
            existingChatLieu.setNgaySua(LocalDateTime.now()); // Cập nhật ngày sửa
            chatLieuRespository.save(existingChatLieu);
        }
        redirectAttributes.addFlashAttribute("UpdateStatusMessage", "Cập nhật thành công !");
        return "redirect:/panda/chatlieu/hienthi";
    }
    // thay đổi trạng thái
    @GetMapping("/change")
    public String changeStatus(@RequestParam("id") int id, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin"; // Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        ChatLieu chatLieu = chatLieuRespository.findById(id).orElse(null);
        if (chatLieu != null) {
            chatLieu.setTrangThai(chatLieu.getTrangThai() == 1 ? 0 : 1); // Đảo ngược trạng thái
            chatLieuRespository.save(chatLieu);
        }
        redirectAttributes.addFlashAttribute("ChangesStatusMessage", "Chuyển trạng thái thành công !");
        return "redirect:/panda/chatlieu/hienthi";
    }
}
