package com.example.demo.respository;

import com.example.demo.entity.HoaDon;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    boolean existsVoucherByMa(String ma);

    boolean existsVoucherByTen(String ten);

    Voucher findByMaAndIdNot(String ma, Integer id);

    Voucher findByTenAndIdNot(String ten, Integer id);

    List<Voucher> findByTrangThaiAndLoaikhachhang(int trangThai, boolean loai);

    Optional<Voucher> findByMa(String ma);



@Query("SELECT vc FROM Voucher vc WHERE " +
        "(?1 IS NULL OR vc.ma LIKE %?1%) AND " +
        "(?2 IS NULL OR vc.ten LIKE %?2%) AND " +
        "(?3 IS NULL OR vc.ngaybatdau >= ?3) AND " +
        "(?4 IS NULL OR vc.ngayketthuc <= ?4) AND " +
        "(?5 IS NULL OR vc.trangThai = ?5) " +
        "ORDER BY vc.ngaytao DESC")
Page<Voucher> findByMaAndTenAndTrangthaiVC(String ma, String ten,LocalDate startDate, LocalDate endDate, Integer trangThai, Pageable pageable);

    @Query(value = "SELECT * FROM voucher where TRANG_THAI = 0 or trang_thai = 1 " +
            "ORDER BY NGAY_TAO DESC", nativeQuery = true)
    List<Voucher> chonVC();
}
