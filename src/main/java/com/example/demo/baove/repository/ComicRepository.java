package com.example.demo.baove.repository;

import com.example.demo.baove.entity.Comic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Integer> {
    List<Comic> findTop5ByOrderByNgayTaoDesc();
    List<Comic> findTop6ByOrderByLuotXemDesc();
    List<Comic> findByTenTruyenContainingIgnoreCase(String tenTruyen);
    Optional<Comic> findByTenTruyen(String tenTruyen);

    @Query("SELECT DISTINCT c FROM Comic c JOIN c.comicDanhMucs cd WHERE cd.danhMuc.id = :id")
    Page<Comic> findByDanhMucId(@Param("id") int id, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Comic c JOIN c.comicDanhMucs cd WHERE cd.danhMuc.id IN :ids")
    Page<Comic> findByDanhMucIds(@Param("ids") List<Integer> ids, Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Comic c where c.TrangThai = 1")
    Page<Comic> findAllWithCollections(Pageable pageable);

    @Query("SELECT c FROM Comic c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(c.tenTruyen) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:trangThai IS NULL OR c.TrangThai = :trangThai)")
    Page<Comic> searchComicsForAdmin(
            @Param("keyword") String keyword,
            @Param("trangThai") Byte trangThai,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Comic c " +
           "LEFT JOIN c.comicTacGias ctg " +
           "LEFT JOIN c.comicDanhMucs cdm " +
           "WHERE (:translatorUsername IS NULL OR c.translator.username = :translatorUsername) " +
           "AND (:trangThai IS NULL OR c.TrangThai = :trangThai) " +
           "AND (:categoryId IS NULL OR cdm.danhMuc.id = :categoryId) " +
           "AND (:authorId IS NULL OR ctg.tacGia.id = :authorId) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(c.tenTruyen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(ctg.tacGia.tenTacGia) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(c.translator.username) LIKE LOWER(CONCAT('%', :keyword, '%')))",
           countQuery = "SELECT COUNT(DISTINCT c) FROM Comic c " +
           "LEFT JOIN c.comicTacGias ctg " +
           "LEFT JOIN c.comicDanhMucs cdm " +
           "WHERE (:translatorUsername IS NULL OR c.translator.username = :translatorUsername) " +
           "AND (:trangThai IS NULL OR c.TrangThai = :trangThai) " +
           "AND (:categoryId IS NULL OR cdm.danhMuc.id = :categoryId) " +
           "AND (:authorId IS NULL OR ctg.tacGia.id = :authorId) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(c.tenTruyen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(ctg.tacGia.tenTacGia) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(c.translator.username) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Comic> searchComicsAdvanced(
            @Param("translatorUsername") String translatorUsername,
            @Param("keyword") String keyword,
            @Param("trangThai") Byte trangThai,
            @Param("categoryId") Integer categoryId,
            @Param("authorId") Integer authorId,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.luotXem), 0) FROM Comic c")
    long sumTotalViews();
}
