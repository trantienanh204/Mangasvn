package com.example.demo.respository;

import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.MauSac;
import com.example.demo.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {
    @Query("SELECT c FROM SanPhamChiTiet c WHERE c.sanPham.id = :sanPhamId")
    List<SanPhamChiTiet> findBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT MIN(spct.dongia) AS minPrice, MAX(spct.dongia) AS maxPrice FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :sanPhamId")
    List<Object[]> findMinAndMaxPriceBySanPhamId(@Param("sanPhamId") Integer sanPhamId);


    @Query("SELECT DISTINCT spct.mauSac FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :sanPhamId")
    List<MauSac> findColorsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);
    @Query("SELECT DISTINCT spct.kichThuoc FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :sanPhamId")
    List<KichThuoc> findkichThuocsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);
    @Query("SELECT DISTINCT spct.kichThuoc, spct.mauSac FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :sanPhamId")
    List<Object[]> findSizesAndColorsBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

//    @Query("SELECT new map(s as size, c as color, p.donGia as price, sc.quantity as quantity) " +
//            "FROM SanPhamChiTiet p " +
//            "JOIN p.kichThuoc s " +
//            "JOIN p.mauSac c " +
//            "JOIN p.sanPhamChiTietQuantity sc " + // giả sử bạn có bảng quan hệ này
//            "WHERE p.sanPham.id = :productId")
//    List<Map<String, Object>> getSizesAndColorsWithPriceAndQuantity(@Param("productId") Integer productId);

    @Query("SELECT MAX(h.maspct) FROM SanPhamChiTiet h")
    String findMaxspct();

        // Giả sử bạn có bảng SanPhamChiTiet với các cột productId, sizeId, colorId và price
        @Query("SELECT spc.dongia FROM SanPhamChiTiet spc WHERE spc.sanPham.id = :productId AND spc.kichThuoc.id = :sizeId AND spc.mauSac.id = :colorId")
        Double findPriceByProductSizeColor(@Param("productId") Integer productId,
                                           @Param("sizeId") Integer sizeId,
                                           @Param("colorId") Integer colorId);

    @Query("SELECT hdct FROM SanPhamChiTiet hdct WHERE hdct.sanPham.id = :id")
    List<SanPhamChiTiet> findsanphamct(@Param("id") int id);

    @Query("""
        SELECT sp FROM SanPhamChiTiet sp 
        WHERE LOWER(sp.sanPham.tensp) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<SanPhamChiTiet> findByTenSanPham(String keyword);


    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.kichThuoc.id = :sizeId AND sp.mauSac.id = :colorId AND sp.sanPham.id = :productId")
    SanPhamChiTiet findBySizeIdColorIdAndProductId(@Param("sizeId") Integer sizeId, @Param("colorId") Integer colorId, @Param("productId") Integer productId);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE LOWER(spct.sanPham.tensp) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(spct.sanPham.masp) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPhamChiTiet> timtenspvama(@Param("keyword") String keyword);



        SanPhamChiTiet findByKichThuocIdAndMauSacIdAndSanPhamId(Integer sizeId, Integer colorId, Integer productId);



    SanPhamChiTiet findByMaspct(String ma);


}

