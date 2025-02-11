package com.example.demo.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ExportExcelService {

    public void exportDataToExcel(
            BigDecimal totalRevenue,
            Map<Integer, BigDecimal> monthlyRevenue,
            Map<LocalDate, BigDecimal> dailyRevenue,
            String filePath) throws IOException {

        // Tạo workbook
        Workbook workbook = new XSSFWorkbook();

        // Tạo sheet "Tổng quan doanh thu"
        Sheet totalRevenueSheet = workbook.createSheet("Tổng quan doanh thu");

        // Ghi tổng doanh thu
        Row totalRow = totalRevenueSheet.createRow(0);
        totalRow.createCell(0).setCellValue("Tổng doanh thu:");
        totalRow.createCell(1).setCellValue(totalRevenue.doubleValue());

        // Tạo sheet "Doanh thu theo tháng"
        Sheet monthlyRevenueSheet = workbook.createSheet("Doanh thu theo tháng");
        Row headerMonthly = monthlyRevenueSheet.createRow(0);
        headerMonthly.createCell(0).setCellValue("Tháng");
        headerMonthly.createCell(1).setCellValue("Doanh thu (VND)");

        int monthlyRowIdx = 1;
        for (Map.Entry<Integer, BigDecimal> entry : monthlyRevenue.entrySet()) {
            Row row = monthlyRevenueSheet.createRow(monthlyRowIdx++);
            row.createCell(0).setCellValue("Tháng " + entry.getKey());
            row.createCell(1).setCellValue(entry.getValue().doubleValue());
        }

        // Tạo sheet "Doanh thu theo ngày"
        Sheet dailyRevenueSheet = workbook.createSheet("Doanh thu 30 ngày qua");
        Row headerDaily = dailyRevenueSheet.createRow(0);
        headerDaily.createCell(0).setCellValue("Ngày");
        headerDaily.createCell(1).setCellValue("Doanh thu (VND)");

        int dailyRowIdx = 1;
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyRevenue.entrySet()) {
            Row row = dailyRevenueSheet.createRow(dailyRowIdx++);
            row.createCell(0).setCellValue(entry.getKey().toString());
            row.createCell(1).setCellValue(entry.getValue().doubleValue());
        }

        // Ghi workbook vào file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        // Đóng workbook
        workbook.close();
    }
}