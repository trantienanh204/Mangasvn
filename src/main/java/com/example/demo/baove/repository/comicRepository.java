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
public interface comicRepository extends JpaRepository<Comic,Integer> {
    List<Comic> findTop5ByOrderByNgayTaoDesc();
    List<Comic> findTop6ByOrderByLuotXemDesc();
    List<Comic> findByTenTruyenContainingIgnoreCase(String tenTruyen);
    Optional<Comic> findByTenTruyen(String tenTruyen);


    @Query("SELECT DISTINCT c FROM Comic c JOIN c.comicDanhMucs cd WHERE cd.danhMuc.id = :id")
    Page<Comic> findByDanhMucId(@Param("id") int id, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Comic c JOIN c.comicDanhMucs cd WHERE cd.danhMuc.id IN :ids")
    Page<Comic> findByDanhMucIds(@Param("ids") List<Integer> ids, Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Comic c")
    Page<Comic> findAllWithCollections(Pageable pageable);

}
