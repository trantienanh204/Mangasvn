package com.example.demo.service;

import com.example.demo.entity.TaiKhoan;
import com.example.demo.respository.TaiKhoanRepo;
import com.example.demo.respository.nhanvienRepository;
import com.example.demo.entity.NhanVien;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class QuenmatkhauService {
@Autowired
    nhanvienRepository nhanvienRepository;
    @Autowired
    TaiKhoanRepo taiKhoanRepo;

     JavaMailSender mailSender;

    public QuenmatkhauService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }
    public String getCaptchaForEmail(String email) {
        return macapchaList.get(email);
    }

    private final Map<String,String> macapchaList = new HashMap<>();
    private final Map<String, Long> timeStampMap = new HashMap<>();
    @Scheduled(fixedRate = 60000)
    public void removeOTP() {
        long currentTime = System.currentTimeMillis();
        macapchaList.entrySet().removeIf(entry -> {
            String email = entry.getKey();
            return currentTime - timeStampMap.get(email) > 30000;

        });
    }

    public void guimail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("conbotthoiok@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(mimeMessage);

    }

    public String random() {
        String randomString;
        int length = 6;
        Random rd = new Random();
        String characters = "0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = rd.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);

        }
        randomString = sb.toString();

        return randomString;
    }

    public Boolean timkiemcapcha(String taikhoan, String capchaCheck) {
        return macapchaList.containsKey(taikhoan) && macapchaList.get(taikhoan).equals(capchaCheck);
    }

    public TaiKhoan timkiemnhanvien(String taikhoan) {
        return taiKhoanRepo.findByTenDangNhap(taikhoan);

    }


    public boolean sendEmail( String toEmail) {
        boolean checkemil = false;
        String subject = "nhận mã OTP bạn ơi  ";
        String OTP =this.random();
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Thông báo mã CAPTCHA</title>\n" +
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
                "        <h2>Đây là mã OTP của bạn</h2>\n" +
                "        <div class=\"captcha-code\">\n" +
                OTP +
                "        </div>\n" +
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
            macapchaList.put(toEmail,OTP);
            timeStampMap.put(toEmail, System.currentTimeMillis());
            this.guimail(toEmail, subject, body);
     checkemil = true ;
     return checkemil;
            // return "Email đã gửi thành cmn công " + toEmail;
        } catch (MessagingException e) {
            e.printStackTrace();
            return checkemil;
          //  return "email gửi thất bại rồi bro: " + e.getMessage();
        }
    }
    public void themnhanvien(TaiKhoan taiKhoan){
        taiKhoanRepo.save(taiKhoan);
    }
    public void updateCaptchaForEmail(String email, String OTP) {
        macapchaList.put(email, OTP);
        timeStampMap.put(email, System.currentTimeMillis());
    }

}

