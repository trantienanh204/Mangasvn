package com.example.demo.Controller.admin;

import com.example.demo.services.ExportExcelService;
import com.example.demo.services.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
@RequestMapping("/panda")
public class ThongKeController {
    @Autowired
    ThongKeService thongKeService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ExportExcelService exportExcelService;

    @GetMapping("/thongke")
    public String thongke(Model model) {
        String role = "admin";
        // Tính doanh thu theo tháng cho năm hiện tại
        int currentYear = LocalDate.now().getYear();
        Map<Integer, BigDecimal> monthlyRevenue = thongKeService.calculateMonthlyRevenue();
        System.out.println("Doanh thu:" + monthlyRevenue);
        // Tổng doanh thu
        BigDecimal totalRevenue = thongKeService.calculateTotalRevenue();
        // Đếm số hóa đơn có trạng thái thành công
        long successfulProductsCount = thongKeService.countSuccessfulProducts();
        // Định dạng doanh thu
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedRevenue = currencyFormat.format(totalRevenue);
        // Đếm số lượng khách hàng
        long totalCustomers = thongKeService.countTotalCustomers();
        // Truyền dữ liệu sang html
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalRevenue", formattedRevenue);
        model.addAttribute("successfulProductsCount", successfulProductsCount);

        List<Object[]> productData = thongKeService.findProductNameAndQuantity();
        // Chuyển đổi dữ liệu thành JSON
        String jsonProductData = null;
        try {
            jsonProductData = objectMapper.writeValueAsString(productData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // In ra log để kiểm tra dữ liệu
        System.out.println("Dữ liệu sản phẩm: " + productData.size());
        // doanh thu theo tháng
        // Chuyển đổi monthlyRevenue sang JSON
        String jsonMonthlyRevenue = null;
        try {
            jsonMonthlyRevenue = objectMapper.writeValueAsString(monthlyRevenue);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // Log dữ liệu JSON
        System.out.println("Dữ liệu doanh thu JSON: " + jsonMonthlyRevenue);

        // Gửi dữ liệu JSON vào model
        model.addAttribute("monthlyRevenue", jsonMonthlyRevenue);
        model.addAttribute("productData", jsonProductData);
        model.addAttribute("role", role);
        return "/admin/ThongKe";
    }
    // tk theo ngay
    @GetMapping("/thongkeDoanhThu")
    public ResponseEntity<?> thongKeTheoNgay(@RequestParam("startDate") String startDate,
                                             @RequestParam("endDate") String endDate) {
        // Chuyển đổi ngày từ String sang LocalDate
        LocalDate start, end;
        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format");
        }

        // Thống kê doanh thu theo ngày
        Map<LocalDate, BigDecimal> dailyRevenue = thongKeService.thongKeDoanhThuTheoNgay(start, end);

        // Trả lại dữ liệu dạng JSON
        return ResponseEntity.ok(dailyRevenue);
    }
    @GetMapping("/thongkeDoanhThuTheoThang")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue(@RequestParam("month") String month) {
        try {
            // Xác định ngày bắt đầu và kết thúc của tháng
            LocalDate startDate = LocalDate.parse(month + "-01");
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // Gọi Service để lấy dữ liệu doanh thu
            List<Map<String, Object>> revenueData = thongKeService.getDailyRevenue(startDate, endDate);

            // Trả về dữ liệu doanh thu dưới dạng JSON
            return ResponseEntity.ok(revenueData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList()); // Trả về danh sách rỗng nếu có lỗi
        }
    }
    @GetMapping("/thongkeDoanhThuTheoNam")
    @ResponseBody
    public List<Map<String, Object>> getAnnualRevenue(@RequestParam("year") int year) {
        try {
            return thongKeService.getRevenueByYear(year);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> exportDataToExcel() {
        try {
            BigDecimal totalRevenue = thongKeService.calculateTotalRevenue();
            Map<Integer, BigDecimal> monthlyRevenue = thongKeService.calculateMonthlyRevenue();
            Map<LocalDate, BigDecimal> dailyRevenue = thongKeService.thongKeDoanhThuTheoNgay(
                    LocalDate.now().minusDays(30), LocalDate.now());

            // Tạo tệp Excel
            String filePath = "D://HocTap/thongke.xlsx";
            exportExcelService.exportDataToExcel(totalRevenue, monthlyRevenue, dailyRevenue, filePath);

            // Đọc tệp từ hệ thống tệp và tạo đối tượng Resource
            File file = new File(filePath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // Trả về ResponseEntity với tệp Excel cho người dùng tải về
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            // Trả về thông báo lỗi nếu gặp sự cố khi xuất tệp
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xuất dữ liệu ra Excel.");
        }
    }

}