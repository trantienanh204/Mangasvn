package com.example.demo.respository;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.KichThuoc;
import com.example.demo.entity.ThuongHieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatLieuRespository extends JpaRepository<ChatLieu,Integer> {
    boolean existsChatLieuByMaChatLieu(String maChatLieu);
    boolean existsChatLieuByTenChatLieu(String tenChatLieu);
    Optional<ChatLieu> findBytenChatLieu(String name);

    @Query("SELECT cl FROM ChatLieu cl WHERE " +
            "(?1 IS NULL OR cl.tenChatLieu LIKE %?1%) AND " +
            "(?2 IS NULL OR cl.trangThai = ?2)"+
            "ORDER BY cl.ngayTao DESC")
    Page<ChatLieu> findByTenAndTrangthaiCL(String tencl, Integer trangThai, Pageable pageable);

}
