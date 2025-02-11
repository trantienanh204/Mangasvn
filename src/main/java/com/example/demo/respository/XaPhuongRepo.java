package com.example.demo.respository;

import com.example.demo.entity.XaPhuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XaPhuongRepo extends JpaRepository<XaPhuong,Integer> {
    @Query("SELECT xp FROM XaPhuong xp WHERE xp.quanHuyen.id = :districtId")
    List<XaPhuong> findByDistrictId(@Param("districtId") Integer districtId);
}
