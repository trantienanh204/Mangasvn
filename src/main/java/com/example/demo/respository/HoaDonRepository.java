

package com.example.demo.respository;

import com.example.demo.entity.DonHang;
import com.example.demo.entity.HoaDon;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Repository

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    @Query("SELECT hd FROM HoaDon hd WHERE " +
            "(?1 IS NULL OR hd.mahoadon LIKE %?1%) AND " +
            "(?2 IS NULL OR hd.nhanVien.tennhanvien LIKE %?2%) AND " +

            "(?3 IS NULL OR hd.khachHang.tenkhachhang LIKE %?3%) AND " +
            "(?4 IS NULL OR hd.trangthai = ?4)"+

            "ORDER BY hd.ngaytao DESC")

    Page<HoaDon> findByMaAndTenAndTrangthaiHD(String mahd,String nv, String tenkh, Integer trangThai, Pageable pageable);

    @Query("SELECT HD FROM HoaDon HD WHERE HD.id =:id ")
    HoaDon finid(Integer id);

    @Query("SELECT MAX(h.mahoadon) FROM HoaDon h")
    String findMaxMaHoaDon();

    @Query("SELECT h FROM HoaDon h WHERE h.khachHang IS NULL")
    List<HoaDon> findHoaDonsWithNullId();



    // Doanh thu theo ngày
    @Query("SELECT COALESCE(SUM(hd.tongtien), 0) " +
            "FROM HoaDon hd " +
            "WHERE hd.trangthai = 1 AND hd.ngaymua = :date")

    BigDecimal getRevenueByDate(@Param("date") LocalDate date);

    // Doanh thu theo từng ngày trong khoảng thời gian
    @Query("SELECT hd.ngaymua, COALESCE(SUM(hd.thanhtien), 0) " +
            "FROM HoaDon hd " +
            "WHERE hd.trangthai = 1 AND hd.ngaymua BETWEEN :startDate AND :endDate " +
            "GROUP BY hd.ngaymua " +
            "ORDER BY hd.ngaymua")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Doanh thu theo tháng
    @Query("SELECT COALESCE(SUM(hd.thanhtien), 0) " +
            "FROM HoaDon hd " +
            "WHERE hd.trangthai = 1 AND YEAR(hd.ngaymua) = :year AND MONTH(hd.ngaymua) = :month")
    BigDecimal getRevenueByMonth(@Param("year") int year, @Param("month") int month);

    // Doanh thu theo năm
    @Query("SELECT MONTH(hd.ngaymua), COALESCE(SUM(hd.thanhtien), 0) " +
            "FROM HoaDon hd " +
            "WHERE hd.trangthai = 1 AND YEAR(hd.ngaymua) = :year " +
            "GROUP BY MONTH(hd.ngaymua) " +
            "ORDER BY MONTH(hd.ngaymua)")
    List<Object[]> getMonthlyRevenueByYear(@Param("year") int year);


    @Query ("SELECT hd FROM HoaDon hd WHERE hd.nhanVien IS NULL")
    Page<HoaDon> hienthihd(Pageable pageable);

    @Query("SELECT h FROM HoaDon h WHERE h.khachHang.id IS NULL ORDER BY h.id DESC")
    List<HoaDon> findHoaDonsDesc();

    @Query("SELECT CASE WHEN COUNT(hd) > 0 THEN TRUE ELSE FALSE END " +
            "FROM HoaDon hd WHERE hd.voucher.id = :voucher " +
            "AND hd.khachHang.id = :khachHang ")
    boolean checkmavoucher( int voucher,
                            int khachHang);


//@Query("SELECT CASE WHEN COUNT(hd) > 0 THEN TRUE ELSE FALSE END " +
//            "FROM HoaDon hd WHERE hd.voucher = :voucher " +
//            "AND hd.khachHang = :khachHang " +
//            "AND hd.voucher.loai = true")
//    boolean checkmavoucher(@Param("voucher") Voucher voucher,
//                                        @Param("khachHang") KhachHang khachHang);





//    @Query("SELECT hd FROM HoaDon hd WHERE " +
//            "(?1 IS NULL OR hd.mahoadon LIKE %?1%) AND " +
//            "(?2 IS NULL OR hd.nhanVien.tennhanvien LIKE %?2%) AND " +
//            "(?3 IS NULL OR hd.khachHang.tenkhachhang LIKE %?3%) AND " +
//            "(?4 IS NULL OR hd.trangthai = ?4)"+
//            "ORDER BY hd.ngaytao DESC")
//    Page<HoaDon> findByMaAndTenAndTrangthaiHD(String mahd, String tennv, String tenkh, Integer trangThai, Pageable pageable);
//




}

