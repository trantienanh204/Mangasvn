package com.example.demo.service;

import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.Voucher;
import com.example.demo.respository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VoucherService {
    @Autowired
    VoucherRepository voucherRepository;

    private final int size = 5;

    public Page<Voucher> hienThiVC(int page, String ma, String ten, LocalDate startDate, LocalDate endDate, Integer trangThai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return voucherRepository.findByMaAndTenAndTrangthaiVC(ma, ten, startDate, endDate, trangThai, pageable);
    }

    // Phương thức để cập nhật trạng thái voucher
    public void updateVoucherStatus() {
        LocalDate currentDate = LocalDate.now();
        List<Voucher> vouchers = voucherRepository.findAll();

        for (Voucher voucher : vouchers) {
            LocalDate startDate = voucher.getNgaybatdau();
            LocalDate endDate = voucher.getNgayketthuc();

            // Kiểm tra xem ngày bắt đầu và ngày kết thúc có hợp lệ không
            if (startDate != null && endDate != null) {
                if (currentDate.isBefore(startDate)) {
                    // Nếu ngày hiện tại bé hơn ngày bắt đầu, trạng thái là "Sắp hoạt động"
                    voucher.setTrangThai(0);
                } else if (currentDate.isAfter(endDate)) {
                    // Nếu ngày hiện tại sau ngày kết thúc, trạng thái cũng là "Hết hạn"
                    voucher.setTrangThai(2);
                } else {
                    // Nếu ngày hiện tại nằm trong khoảng ngày bắt đầu đến ngày kết thúc, trạng thái là "Hoạt động"
                    voucher.setTrangThai(1);
                }
                // Lưu lại voucher vào cơ sở dữ liệu
                voucherRepository.save(voucher);
            }
        }
    }

    public List<Voucher> getActivePublicVouchers() {
        // `trangThai = 1` là trạng thái hoạt động và `loai = false` là công khai
        return voucherRepository.findByTrangThaiAndLoaikhachhang(1, true);
    }

}

