package com.example.demo.Controller;


import com.example.demo.entity.ChiTietVaiTro;
import com.example.demo.entity.KhachHang;
import com.example.demo.service.EmailService;
import com.example.demo.services.RegisterService;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.entity.VaiTro;
import com.example.demo.respository.ChiTietVaiTroRepository;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.TaiKhoanRepository;
import com.example.demo.respository.VaiTroRepository;


import com.example.demo.services.RegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.mindrot.jbcrypt.BCrypt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/panda")
public class RegisterController {
    @Autowired

    EmailService emailService;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    TaiKhoanRepository taiKhoanRepository;
    @Autowired
    RegisterService registerService;
    @Autowired
    VaiTroRepository vaiTroRepository;
    @Autowired
    ChiTietVaiTroRepository chiTietVaiTroRepository;


    @GetMapping("/register")
    public String register(HttpSession session,Model model) {
        // Lấy email từ session
        String email = (String) session.getAttribute("email");
        // Truyền dữ liệu sang
        model.addAttribute("email", email);
        return "Register"; // Trả về tên view
    }


    @PostMapping("/register")
    public String register(HttpSession session, KhachHang khachHang,Model model, RedirectAttributes redirectAttributes,
                           @RequestParam("tentaikhoan") String email, @RequestParam("tenkhachhang") String tenkhachhang, @RequestParam("sdt") String sdt ) {

        // check lỗi
        boolean hasErrors = false;
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("emailErrors","Email không đúng định dạng");
            hasErrors = true;
        }
        if (taiKhoanRepository.existsByTenDangNhap(khachHang.getTentaikhoan())){
            model.addAttribute("emailExists","Email đã tồn tại");
            hasErrors = true;
        }
        if (taiKhoanRepository.existsByTenDangNhap(khachHang.getTentaikhoan())){
            model.addAttribute("emailExists","Email đã tồn tại");
            hasErrors = true;
        }
        if (khachHangRepository.existsBySdt(sdt)){
            model.addAttribute("sdtExists","Số điện thoại đã tồn tại");
            hasErrors = true;
        }
        // check trống
        if(email.trim().isEmpty()){
            model.addAttribute("emailEmpty","Email không được để trống");
            hasErrors = true;
        }
        if(tenkhachhang.trim().isEmpty()){
            model.addAttribute("nameEmpty","Họ tên không được để trống");
            hasErrors = true;
        }
        if(sdt.trim().isEmpty()){
            model.addAttribute("numberphoneEmpty","Số điện thoại không được để trống");
            hasErrors = true;
        }
        if (!sdt.matches("^0\\d{9}$")) {
            model.addAttribute("phoneErrors", "Số điện phải thoại bắt đầu bằng 0 và có 10 số");
            hasErrors = true;
        }
//
        // Nếu có lỗi, trả về trang đăng ký với thông báo lỗi
        if (hasErrors) {
            session.setAttribute("email", email); // Đảm bảo email luôn tồn tại trong session
            model.addAttribute("email", email);
            return "Register";
        }
        // Tạo mật khẩu ngẫu nhiên
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        int passwordLength = 8 + new SecureRandom().nextInt(7); // 8 đến 14 ký tự
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        String plainPassword = password.toString(); // Mật khẩu chưa băm
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt()); // Băm mật khẩu

        // Gửi email thông báo mật khẩu cho người dùng
        String subject = "Chào mừng khách hàng" + " " + khachHang.getTenkhachhang() + " đã tạo tài khoản tại shop Panda Shirt!";
        String body = "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }" +
                "h2 { text-align: center; color: #333; }" +
                "p { color: #555; line-height: 1.6; }" +
                ".password { font-size: 24px; font-weight: bold; color: #007bff; text-align: center; margin: 20px 0; padding: 10px; border: 1px solid #007bff; border-radius: 5px; background-color: #e7f3ff; }" +
                ".footer { margin-top: 20px; text-align: center; color: #555; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Chào mừng bạn mở tài khoản tại web bán áo phông Panda Shirt!</h2>" +
                "<p>Mật khẩu của bạn đã được tạo thành công. Vui lòng ghi nhớ nó!</p>" +
                "<div class='password'>" + plainPassword + "</div>" +
                "<p>Bạn có thể đăng nhập vào tài khoản của mình tại <a href='http://localhost:8080/panda/login' style='color: #007bff;'>đây</a>.</p>" +
                "<div class='footer'>Nếu bạn có bất kỳ câu hỏi nào, hãy liên hệ với bộ phận hỗ trợ của chúng tôi.</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        // gửi mail
        emailService.sendEmail(khachHang.getTentaikhoan(), subject, body);
        // Tạo tài khoản và lưu vào cơ sở dữ liệu
        TaiKhoan taiKhoan = new TaiKhoan();
        ChiTietVaiTro chiTietVaiTro = new ChiTietVaiTro();
        String tentk = email;
        // set vai tro
        int vaitro = 3;
        taiKhoan.setMatKhau(hashedPassword);
        taiKhoan.setTenDangNhap(tentk);
        taiKhoanRepository.save(taiKhoan);
        VaiTro vt = vaiTroRepository.findById(vaitro).get();
        TaiKhoan tendangnhap = taiKhoanRepository.findByTenDangNhap(tentk);
        // log
        System.out.println("vaitro " +vt.getTenVaiTro());
        System.out.println("tên " +tendangnhap.getTenDangNhap());
        System.out.println("tên " +tentk);
        chiTietVaiTro.setVaiTro(vt);
        chiTietVaiTro.setTaiKhoan(tendangnhap);
        chiTietVaiTroRepository.save(chiTietVaiTro);
        // lua khach hang vao db
        khachHang.setTaiKhoan(tendangnhap);
        // ma kh
        String maKhachHang = "KH" + UUID.randomUUID().toString().replace("-", "").substring(0, 6); // 6 ký tự từ UUID
        khachHang.setMakhachhang(maKhachHang);

        khachHang.setSdt(sdt);
        khachHang.setTenkhachhang(tenkhachhang);
        //khachHang.setDiachi(diachi);
        khachHang.setDelete(true);
        khachHang.setTinhtrang(true);
        khachHang.setMatkhau(hashedPassword);
        khachHang.setTrangthai(1);
        khachHang.setGioitinh(null);
        khachHang.setNgaytao(LocalDate.now());
        khachHangRepository.save(khachHang);
        // xóa session email
        session.removeAttribute("email");
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng ký thành công!");
        // Redirect sau khi đăng ký thành công
        return "redirect:/panda/login"; // Quay lại trang đăng nhập sau khi đăng ký
    }
}

