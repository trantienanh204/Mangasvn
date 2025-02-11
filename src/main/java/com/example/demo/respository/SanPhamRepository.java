
package com.example.demo.respository;

import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.SanPham;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham,Integer> {


                List<SanPham> findByTenspContainingIgnoreCase(String query);
                List<SanPham> findByTenspContainingIgnoreCaseAndDanhMucId(String query, int categoryId);



        @Query("SELECT sp FROM SanPham sp WHERE " +
                "(?1 IS NULL OR sp.tensp LIKE %?1%) AND " +
                "(?2 IS NULL OR sp.trangthai = ?2)"+
                "ORDER BY sp.ngaytao DESC")
        Page<SanPham> findByTenspAndTrangthai(String tensp, Integer trangthai, Pageable pageable);


        @Query("SELECT s FROM SanPham s WHERE s.sanPhamChiTietList IS NOT EMPTY")
        List<SanPham> findSanPhamWithDetails();


        List<SanPham> findByDanhMuc(DanhMuc danhMuc);


        List<SanPham> findAllByOrderByNgaytaoDesc();


//    @Query("SELECT sp FROM SanPham sp WHERE " +
//            "(?1 IS NULL OR sp.tensp LIKE %?1%) AND " +
//            "(?2 IS NULL OR sp.trangthai = ?2)"+
//            "ORDER BY sp.ngaytao DESC")
//    Page<SanPham> findByTenspAndTrangthai(String tensp, Integer trangthai, Pageable pageable);
//    @Query("SELECT s FROM SanPham s WHERE s.sanPhamChiTietList IS NOT EMPTY")
//    List<SanPham> findSanPhamWithDetails();
//    List<SanPham> findByDanhMuc(DanhMuc danhMuc);
//    List<SanPham> findAllByOrderByNgaytaoDesc();

    // Truy vấn để lấy tên sản phẩm và tổng số lượng từ SanPhamChiTiet
    @Query("SELECT sp.tensp, SUM(spct.soluongsanpham) " +
            "FROM SanPham sp JOIN sp.sanPhamChiTietList spct " +
            "GROUP BY sp.id, sp.tensp")
    List<Object[]> findProductNameAndQuantity();
}

