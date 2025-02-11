package com.example.demo.Controller.login;

import com.example.demo.DTO.KhachHangDTO;
import com.example.demo.DTO.TaiKhoanDTO;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.respository.NhanVienRespository;
import com.example.demo.respository.TaiKhoanRepo;
import com.example.demo.service.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/panda")
public class LoginController {
    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired

    private TaiKhoanRepo taiKhoanRepo;

    TaiKhoanRepo taiKhoan ;

    NhanVien nv = new NhanVien();

    @GetMapping("/checkRoles")
    public String checkRoles(Authentication authentication) {
        encodeAllPasswords();
        return "ok";
    }

    @GetMapping("/mahoa")
    @Transactional
    public void encodeAllPasswords() {
        List<TaiKhoan> tkls = taiKhoan.findAll();
        for (TaiKhoan tk : tkls) {
            String plainPassword = tk.getMatKhau();
            if (!passwordEncoder.matches(plainPassword, plainPassword)) {
                String encodedPassword = passwordEncoder.encode(plainPassword);
                tk.setMatKhau(encodedPassword);
                taiKhoan.save(tk);
            }
        }
    }



    @GetMapping("/login")
    public String showLoginPage() {
        return "Login";  // Hiển thị trang đăng nhập
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute TaiKhoanDTO taiKhoanDTO, Model model, HttpSession session) {
        String tenDangNhap = taiKhoanDTO.getTenDangNhap();
        String matKhau = taiKhoanDTO.getMatKhau();

        // Tìm tài khoản trong cơ sở dữ liệu
        TaiKhoanDTO foundTaiKhoan = taiKhoanService.findByTenDangNhap(tenDangNhap);

        if (foundTaiKhoan == null) {
            model.addAttribute("error", "Tên đăng nhập không tồn tại.");
            return "Login"; // Hiển thị lỗi nếu không tìm thấy tài khoản
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(matKhau, foundTaiKhoan.getMatKhau())) {
            model.addAttribute("error", "Mật khẩu không chính xác.");
            return "Login"; // Hiển thị lỗi nếu mật khẩu không đúng
        }

        // Lấy thông tin khách hàng từ tài khoản
        KhachHangDTO khachHangDTO = foundTaiKhoan.getKhachHangDTO();
        if (khachHangDTO == null) {
            model.addAttribute("error", "Không tìm thấy thông tin khách hàng.");
            return "Login"; // Hiển thị lỗi nếu không tìm thấy thông tin khách hàng
        }

        // Lưu thông tin người dùng vào session
        session.setAttribute("loggedInUser", khachHangDTO);

        // Kiểm tra nếu có mục tiêu chuyển hướng sau khi đăng nhập
        String roomId = (String) session.getAttribute("roomId");
        if (roomId != null) {
            return "redirect:/showRoomDetailPhong?roomId=" + roomId; // Chuyển hướng đến chi tiết phòng nếu có
        }


        // Chuyển hướng đến trang chi tiết phòng nếu roomId có
//        if (roomId != null) {
//            return "redirect:/showRoomDetailPhong?roomId=" + roomId;
//        }
        return "redirect:/";

    }

    @GetMapping("/error")
    public String loi(){
        return "error";
    }
}



