package com.example.demo.respository;

import com.example.demo.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.entity.NhanVien;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

import java.util.Optional;


@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    boolean existsKhachHangByMakhachhang(String makhachhang);

    boolean existsKhachHangBySdt(String sdt);


    //hiển thị tất cả khách hàng ngoại trừ id = 6 là khách lẻ

    @Query("SELECT kh FROM KhachHang kh WHERE " +
            "(?1 IS NULL OR kh.makhachhang LIKE %?1%) AND " +
            "(?2 IS NULL OR kh.tenkhachhang LIKE %?2%) AND " +
            "(?3 IS NULL OR kh.trangthai = ?3) AND " +
            "kh.id <> 6 " +
            "ORDER BY kh.ngaytao DESC")
    Page<KhachHang> findByMaAndTenAndTrangthaiKH(String makh, String tenkh, Integer trangThai, Pageable pageable);

    //    hiển thị các khách hàng có trạng thái là hoạt động ngoại trừ khách hàng id = 6 vì là khách lẻ
    @Query(value = "SELECT * \n" +
            "FROM khach_hang \n" +
            "WHERE id <> 6 AND TRANG_THAI = 1 AND TEN_TAI_KHOAN IS NOT NULL;", nativeQuery = true)
    List<KhachHang> dskhhoatdong();

    @Query("SELECT kh from KhachHang  kh where kh.sdt is not null")
    List<KhachHang> timkhachhang();
//
//    @Query("SELECT kh.tinh_tp.tentinhTP FROM KhachHang kh WHERE kh.id = :id")
//    String findTenTinhByKhachHangId(@Param("id") Integer id);

    Optional<KhachHang> findByTentaikhoan(String name);

    @Query("SELECT kh FROM KhachHang kh WHERE kh.tentaikhoan = :tenTaiKhoan")
    Optional<KhachHang> findByTenTaiKhoan(@Param("tenTaiKhoan") String tenTaiKhoan);

    boolean existsByTentaikhoan(String tentaikhoan);

    boolean existsBySdt(String sdt);


    KhachHang findBySdt(String sdt);



        @Query("SELECT k FROM KhachHang k WHERE k.sdt = :sdt AND k.id <> :id")
        KhachHang findBySdtAndNotId(@Param("sdt") String sdt, @Param("id") Integer id);




    @Query("SELECT MAX(kh.makhachhang) FROM KhachHang kh")
    String findMaxMakh();


}
