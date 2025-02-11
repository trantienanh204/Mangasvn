package com.example.demo.Controller.quenmatkhau;


import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.service.QuenmatkhauService;


    import jakarta.mail.MessagingException;
    import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("quenmatkhau")
public class quenmatkhauController {

    @Autowired
    private QuenmatkhauService quenmatkhauService;

    @GetMapping("/timtaikhoan")
    public String timtaikhoan() {
        return "quenmatkhau/QuenMatKhai";
    }

    @PostMapping("/guimail")
    public String guimail(@RequestParam("tentaikhoan") String tentaikhoan, HttpSession session, Model model) {
        String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (tentaikhoan == null || tentaikhoan.isEmpty() || !tentaikhoan.matches(regexEmail)) {
            model.addAttribute("errorMessage", "Tên tài khoản không hợp lệ. Vui lòng nhập địa chỉ email hợp lệ.");
            return "quenmatkhau/QuenMatKhai";
        }

        TaiKhoan nhanVien = quenmatkhauService.timkiemnhanvien(tentaikhoan);

        if (nhanVien != null) {
            String toEmail = nhanVien.getTenDangNhap();
            if (quenmatkhauService.sendEmail(toEmail)) {
                session.setAttribute("email", toEmail);
                return "quenmatkhau/nhapcapcha";
            } else {
                model.addAttribute("errorMessage", "Gửi email thất bại. Vui lòng thử lại.");
                return "quenmatkhau/QuenMatKhai";
            }
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy tài khoản với thông tin đã nhập.");
            return "quenmatkhau/QuenMatKhai";
        }
    }


    @PostMapping("/xacnhan")
    public String xacNhanOTP(@RequestParam("OTP") String OTP, HttpSession session, Model model) {
        //ờmmmmmmmmmmmmmmmmmmmmmmmmmmm
        String email = (String) session.getAttribute("email");

        String maCaptchaDaGui = quenmatkhauService.getCaptchaForEmail(email);

        if (maCaptchaDaGui != null && maCaptchaDaGui.equals(OTP)) {
            model.addAttribute("successMessage", "Xác nhận thành công!");
            return "quenmatkhau/DoiMatKhau";
        } else {
            model.addAttribute("errorMessage", "Mã CAPTCHA không chính xác. Vui lòng thử lại.");
            return "quenmatkhau/nhapcapcha";
        }
    }

    @PostMapping("/doimatkhau")
    public String doiMatKhau(@RequestParam("matkhaumoi") String matkhaumoi,
                             @RequestParam("matkhauXacNhan") String matkhauXacNhan,
                             HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        TaiKhoan taiKhoan = quenmatkhauService.timkiemnhanvien(email);

        if (taiKhoan != null) {
            if (matkhaumoi.equals(matkhauXacNhan)) {
                String hashedPassword = BCrypt.hashpw(matkhauXacNhan, BCrypt.gensalt());
                taiKhoan.setMatKhau(hashedPassword);
                quenmatkhauService.themnhanvien(taiKhoan);
                model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
                return "Login";
            } else {
                model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp. Vui lòng thử lại.");
                return "quenmatkhau/DoiMatKhau";
            }
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy tài khoản.");
            return "quenmatkhau/DoiMatKhau";
        }
    }

    @PostMapping("/guiLaiMa")
    public String guiLaiMa(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            boolean sent = quenmatkhauService.sendEmail(email);
            if (sent) {
                String otp = quenmatkhauService.getCaptchaForEmail(email);
                quenmatkhauService.updateCaptchaForEmail(email, otp);
                model.addAttribute("successMessage", "Mã OTP mới đã được gửi đến email của bạn.");
            } else {
                model.addAttribute("errorMessage", "Gửi lại mã OTP thất bại. Vui lòng thử lại.");
            }
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy email để gửi mã.");
        }
        return "quenmatkhau/nhapcapcha";
    }

    @ResponseBody
 @PostMapping("/listOTPEmail")
    public void listOTPEmail(@RequestParam("email")String email){
            String subject = "nhận mã OTP bạn ơi  ";
            String OTP ="test thu thoi";
            String body = "<!DOCTYPE html>\n" +
                    "<html lang=\"vi\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Thông báo mã khuyen mai</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            background-color: #f4f4f4;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "        .email-container {\n" +
                    "            background-color: #ffffff;\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 0 auto;\n" +
                    "            padding: 20px;\n" +
                    "            border-radius: 8px;\n" +
                    "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);\n" +
                    "        }\n" +
                    "        .header {\n" +
                    "            text-align: center;\n" +
                    "            background-color: #FF8C00;\n" +
                    "            color: white;\n" +
                    "            padding: 10px;\n" +
                    "            border-radius: 8px 8px 0 0;\n" +
                    "        }\n" +
                    "        .header h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "            text-align: center;\n" +
                    "        }\n" +
                    "        .content h2 {\n" +
                    "            color: #333;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .captcha-code {\n" +
                    "            display: inline-block;\n" +
                    "            background-color: #f7f7f7;\n" +
                    "            font-size: 24px;\n" +
                    "            letter-spacing: 2px;\n" +
                    "            color: #007bff;\n" +
                    "            padding: 10px 20px;\n" +
                    "            border: 2px dashed #007bff;\n" +
                    "            border-radius: 8px;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .content p {\n" +
                    "            color: #555;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #aaa;\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div class=\"email-container\">\n" +
                    "    <div class=\"header\">\n" +
                    "        <h1>Xác nhận OTP</h1>\n" +
                    "    </div>\n" +
                    "    <div class=\"content\">\n" +
                    "        <h2>Đây là mã khuyến mãi của bạn</h2>\n" +
                    "        <div class=\"captcha-code\">\n" +
                    OTP +
                    "        </div>\n" +
                    "nhập bừa thông báo chơi cho vui thôi , nội dun" +
                    "        <p>Vui lòng nhập mã OTP này để xác nhận.</p>\n" +
                    "    </div>\n" +
                    "    <div class=\"footer\">\n" +
                    "        <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n";

            try {

                quenmatkhauService.guimail(email, subject, body);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }






    //quenmatkhauService.macapchaList.add(Integer.parseInt(captchaCode));

//    @PostMapping("/listOTPEmail")
//    public String listOTPEmail(@RequestParam("email") String email) {
//        return quenmatkhauService.getCaptchaForEmail(email);
//
//    }


    //quenmatkhauService.macapchaList.add(Integer.parseInt(captchaCode));
}


