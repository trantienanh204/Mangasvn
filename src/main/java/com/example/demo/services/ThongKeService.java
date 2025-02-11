package com.example.demo.services;

import com.example.demo.entity.HoaDon;
import com.example.demo.respository.HoaDonRepository;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThongKeService {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Tìm tổng số sản phẩm và số lượng
//    public List<Object[]> findProductNameAndQuantity() {
//        List<Object[]> productData = sanPhamRepository.findAll().stream()
//                .map(sp -> new Object[]{sp.getTensp(), sp.getSoluongsp()})
//                .collect(Collectors.toList());
//
//        System.out.println("Số lượng sản phẩm lấy được: " + productData.size());
//        return productData;
//    }
    public List<Object[]> findProductNameAndQuantity() {
        // Gọi repository để lấy dữ liệu
        List<Object[]> productData = sanPhamRepository.findProductNameAndQuantity();

        // Log dữ liệu để kiểm tra
        System.out.println("Số lượng sản phẩm lấy được: " + productData.size());
        productData.forEach(data -> {
            System.out.println("Tên sản phẩm: " + data[0] + ", Tổng số lượng: " + data[1]);
        });

        return productData;
    }

    // Tính tổng doanh thu
    public BigDecimal calculateTotalRevenue() {
        return hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getTrangthai() == 1
                        && hd.getNhanVien() != null
                        && hd.getKhachHang() != null) // Kiểm tra nhân viên và khách hàng không null
                .map(hd -> hd.getThanhtien() != null ? hd.getThanhtien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Tính doanh thu theo tháng
    public Map<Integer, BigDecimal> calculateMonthlyRevenue() {
        return hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getTrangthai() == 1
                        && hd.getNhanVien() != null
                        && hd.getKhachHang() != null) // Kiểm tra nhân viên và khách hàng không null
                .collect(Collectors.groupingBy(
                        hd -> hd.getNgaymua().getMonthValue(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                hd -> hd.getThanhtien() != null ? hd.getThanhtien() : BigDecimal.ZERO,
                                BigDecimal::add
                        )
                ));
    }

    // Đếm số hóa đơn thành công
    public long countSuccessfulProducts() {
        return hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getTrangthai() == 1
                        && hd.getNhanVien() != null
                        && hd.getKhachHang() != null) // Kiểm tra nhân viên và khách hàng không null
                .count();
    }

    // Đếm tổng số khách hàng
    public long countTotalCustomers() {
        return khachHangRepository.count();
    }

    // Thống kê doanh thu theo ngày
    public Map<LocalDate, BigDecimal> thongKeDoanhThuTheoNgay(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        Map<LocalDate, BigDecimal> doanhThuTheoNgay = new LinkedHashMap<>();

        // Khởi tạo doanh thu mặc định là 0 cho mỗi ngày
        LocalDate ngayHienTai = ngayBatDau;
        while (!ngayHienTai.isAfter(ngayKetThuc)) {
            doanhThuTheoNgay.put(ngayHienTai, BigDecimal.ZERO);
            ngayHienTai = ngayHienTai.plusDays(1);
        }

        // Lọc hóa đơn
        List<HoaDon> danhSachHoaDon = hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getTrangthai() == 1
                        && hd.getNhanVien() != null
                        && hd.getKhachHang() != null) // Kiểm tra nhân viên và khách hàng không null
                .collect(Collectors.toList());

        for (HoaDon hoaDon : danhSachHoaDon) {
            LocalDate ngayMua = hoaDon.getNgaymua();
            if (!ngayMua.isBefore(ngayBatDau) && !ngayMua.isAfter(ngayKetThuc)) {
                BigDecimal doanhThu = hoaDon.getThanhtien() != null ? hoaDon.getThanhtien() : BigDecimal.ZERO;
                doanhThuTheoNgay.put(ngayMua, doanhThuTheoNgay.get(ngayMua).add(doanhThu));
            }
        }
        return doanhThuTheoNgay;
    }

    // Doanh thu theo ngày
    public List<Map<String, Object>> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> revenueList = new ArrayList<>();

        List<HoaDon> hoaDons = hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getTrangthai() == 1
                        && hd.getNhanVien() != null
                        && hd.getKhachHang() != null
                        && !hd.getNgaymua().isBefore(startDate)
                        && !hd.getNgaymua().isAfter(endDate))
                .collect(Collectors.toList());

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Map<String, Object> revenueData = new HashMap<>();
            revenueData.put("date", currentDate.toString());

            LocalDate finalCurrentDate = currentDate;
            BigDecimal totalRevenueForDay = hoaDons.stream()
                    .filter(hd -> hd.getNgaymua().equals(finalCurrentDate))
                    .map(hd -> hd.getThanhtien() != null ? hd.getThanhtien() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            revenueData.put("revenue", totalRevenueForDay);
            revenueList.add(revenueData);

            currentDate = currentDate.plusDays(1);
        }
        return revenueList;
    }

    // Doanh thu theo từng tháng trong năm
    public List<Map<String, Object>> getRevenueByYear(int year) {
        List<Map<String, Object>> revenueList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            BigDecimal monthlyRevenue = hoaDonRepository.getRevenueByMonth(year, month);
            Map<String, Object> revenueData = new HashMap<>();
            revenueData.put("month", "Tháng " + month);
            revenueData.put("revenue", monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);
            revenueList.add(revenueData);
        }
        return revenueList;
    }
}
