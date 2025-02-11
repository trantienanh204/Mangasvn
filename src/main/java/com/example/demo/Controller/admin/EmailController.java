package com.example.demo.Controller.admin;

import com.example.demo.entity.Customer;
import com.example.demo.entity.EmailRequest;
import com.example.demo.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping("/sendemails")
    public ResponseEntity<String> sendVoucherEmails(@RequestBody EmailRequest emailRequest) {
        String voucherId = emailRequest.getVoucherId();
        List<Customer> customers = emailRequest.getKhachHangList();

        String subject = "Nhận mã Voucher từ Panda-Shirt nào bạn ơi";
        String MaVC = emailService.layvoucher(Integer.parseInt(voucherId)).getMa();
        String Mota = emailService.layvoucher(Integer.parseInt(voucherId)).getMota();
        String Dk = emailService.layvoucher(Integer.parseInt(voucherId)).getDieuKien();
        String MG = emailService.layvoucher(Integer.parseInt(voucherId)).getMucGiam();
        Boolean Loai = emailService.layvoucher(Integer.parseInt(voucherId)).isLoai();
        LocalDate NBD = emailService.layvoucher(Integer.parseInt(voucherId)).getNgaybatdau();
        LocalDate NKT = emailService.layvoucher(Integer.parseInt(voucherId)).getNgayketthuc();

        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Thông báo mã Voucher từ Panda-Shirt</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css\"/>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f0f0f0;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            text-align: center;\n" +  // Căn giữa toàn bộ nội dung trong body
                "        }\n" +
                "        .email-container {\n" +
                "            background-color: #ffffff;\n" +
                "            max-width: 650px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 30px;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);\n" +
                "            font-size: 16px;\n" +
                "            color: #333;\n" +
                "            text-align: left;\n" +  // Căn trái các nội dung bên trong email container
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            background-color: #FF8C00;\n" +
                "            color: white;\n" +
                "            padding: 30px 0;\n" +
                "            border-radius: 10px 10px 0 0;\n" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .header h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 28px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 25px;\n" +
                "            text-align: center;\n" +  // Căn giữa nội dung trong phần content
                "        }\n" +
                "        .content h2 {\n" +
                "            color: #333;\n" +
                "            margin-bottom: 20px;\n" +
                "            font-size: 22px;\n" +
                "        }\n" +
                "        .voucher-code {\n" +
                "            display: inline-block;\n" +
                "            background-color: #f7f7f7;\n" +
                "            font-size: 32px;\n" +
                "            letter-spacing: 3px;\n" +
                "            color: #007bff;\n" +
                "            padding: 20px 40px;\n" +
                "            border: 2px solid #007bff;\n" +
                "            border-radius: 12px;\n" +
                "            margin-bottom: 30px;\n" +
                "            font-weight: bold;\n" +
                "            transition: all 0.3s ease;\n" +
                "        }\n" +
                "        .voucher-code:hover {\n" +
                "            background-color: #007bff;\n" +
                "            color: white;\n" +
                "            cursor: pointer;\n" +
                "            transform: translateY(-5px);\n" +
                "        }\n" +
                "        .noidung {\n" +
                "            font-size: 16px;\n" +
                "            margin-bottom: 15px;\n" +
                "            color: #555;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            font-size: 14px;\n" +
                "            color: #888;\n" +
                "            margin-top: 40px;\n" +
                "        }\n" +
                "        .footer p {\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"email-container\">\n" +
                "    <div class=\"header\">\n" +
                "        <h1>Ưu đãi từ Panda-Shirt</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "        <h2>Voucher đặc biệt dành cho bạn</h2>\n" +
                "        <div class=\"voucher-code\">\n" +
                MaVC +
                "        </div>\n" +
                "        <div class=\"noidung\">\n" +
                "            <b>Mức giảm: </b>" + MG + (Loai ? " %" : " VND") +
                "        </div>\n" +
                "        <div class=\"noidung\">\n" +
                "            <b>Điều kiện áp dụng: </b> Tổng đơn hàng phải lớn hơn <b>" + Dk + "</b>VND mới có thể sử dụng mã giảm giá này.\n" +
                "        </div>\n" +
                "        <div class=\"noidung\">\n" +
                "            <b>Mô tả voucher: </b>" + Mota + "\n" +
                "        </div>\n" +
                "        <div class=\"noidung\">\n" +
                "            <b>Từ ngày:</b> " + NBD + "\n" +
                "        </div>\n" +
                "        <div class=\"noidung\">\n" +
                "            <b>Đến ngày:</b> " + NKT + "\n" +
                "        </div>\n" +
                "        <p>Vui lòng nhập mã Voucher này để nhận ưu đãi. Chúc bạn mua sắm vui vẻ!</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "        <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";

        // Gửi email cho từng khách hàng trong danh sách
        for (Customer customer : customers) {
            try {
                emailService.guimail(customer.getTentaikhoan(), subject, body);
            } catch (MessagingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gửi email thất bại cho: " + customer.getTentaikhoan());
            }
        }
        return ResponseEntity.ok("{ \"message\": \"Mã giảm giá đã được gửi thành công!\" }");

    }
}
