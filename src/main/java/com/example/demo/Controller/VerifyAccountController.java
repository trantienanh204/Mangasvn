package com.example.demo.Controller;

import com.example.demo.respository.KhachHangRepository;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;

@Controller
@RequestMapping("/panda")
public class VerifyAccountController {
    @Autowired
    EmailService emailService;
    @Autowired
    KhachHangRepository khachHangRepository;
    // random otp ngẫu nhiên
    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
    @GetMapping("/verify")
    public String showVerifyAccount(){
        return "VerifyAccount";
    }
    @PostMapping("/verify")
    public String verifyAccount(HttpSession session, Model model, @RequestParam("email") String email, RedirectAttributes redirectAttributes){
        // check valid
        boolean hasErrors = false;
        if (email.trim().isEmpty()){
            model.addAttribute("emailEmpty","Vui lòng nhập email !");
           hasErrors = true;
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("emailFormat","Email không đúng định dạng !");
            hasErrors = true;
        }
        if (hasErrors) {
            return "VerifyAccount";
        }
        // lưa email
        session.setAttribute("email",email);
        session.setAttribute("otpCreationTime", System.currentTimeMillis()); // Thời gian tạo OTP (ms)
        // otp
        String otp = generateOTP();
        // lưa mã otp vào session
        session.setAttribute("otp",otp);
        // gửi otp cho khách hàng
        // Gửi email thông báo mật khẩu cho người dùng
        String subject = "Chào mừng khách hàng mới !";
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
                "<p>Mã otp xác nhận của bạn là:"+
                "<div class='password'>" + otp + "</div>" +
                "<p>Mã otp xác nhận của bạn có hiệu lực trong vòng 1 phút, vui lòng lưa ý !</a>.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
        // gửi mail
        emailService.sendEmail(email, subject, body);
        return "redirect:/panda/verifyOtp";
    }
    // show form nhap otp
    @GetMapping("/verifyOtp")
    public String showVerifyOtp(){
        return "VerifyAccountOTP";
    }
    // xử lý otp
    @PostMapping("/verifyOtp")
    public String verifyOtp(@RequestParam("otp") String inputOtp, HttpSession session, RedirectAttributes redirectAttributes,Model model){
        // lấy mã otp đã lưa ở session
        String sessionOtp = (String) session.getAttribute("otp");
        // Thời gian tồn tại của otp
        Long otpCreationTime = (Long) session.getAttribute("otpCreationTime");
        // kiểm tra mã otp
        if (sessionOtp == null || otpCreationTime == null ||
                (System.currentTimeMillis() - otpCreationTime > 60000)) { // 60000 ms = 1 phút
            redirectAttributes.addFlashAttribute("error", "Mã OTP đã hết hạn !");

            // Xóa OTP và thời gian tạo khỏi session
            session.removeAttribute("otp");
            session.removeAttribute("otpCreationTime");

            return "redirect:/panda/verify"; // Quay lại trang nhập email
        }
        if (sessionOtp != null && sessionOtp.equals(inputOtp)) {
            // Xóa mã OTP khỏi session sau khi xác minh thành công
            session.removeAttribute("otp");

            // Chuyển hướng đến trang nhập thông tin người dùng
            return "redirect:/panda/register";
        }
        // Check otp nếu người dùng không nhập
        if (inputOtp == null || inputOtp.trim().isEmpty()){
            redirectAttributes.addFlashAttribute("OtpEmpty", "Vui lòng nhập mã otp !");
            return "redirect:/panda/verifyOtp"; // Quay lại trang nhập OTP
        }
        // Check otp sai
        if (!sessionOtp.equals(inputOtp)){
            redirectAttributes.addFlashAttribute("OtpError", "Mã OTP không chính xác, vui lòng thử lại !");
            return "redirect:/panda/verifyOtp"; // Quay lại trang nhập OTP
        }

        // Nếu mã OTP không hợp lệ, hiển thị thông báo lỗi
        redirectAttributes.addFlashAttribute("OTPError", "Mã OTP không hợp lệ !");
        return "VerifyAccountOTP";

    }
}
