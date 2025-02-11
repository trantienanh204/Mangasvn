package com.example.demo.service;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.KichThuoc;
import com.example.demo.respository.ChatLieuRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatLieuService {
    @Autowired
    ChatLieuRespository chatLieuRespository;
    public List<ChatLieu> list() {
        return chatLieuRespository.findAll();
    }
    public void add(ChatLieu chatLieu) {
        chatLieuRespository.save(chatLieu);
    }

    public void update(ChatLieu chatLieu) {
        chatLieuRespository.save(chatLieu);
    }

    private final int size = 5;
    public Page<ChatLieu> hienThiCL(int page, String tencl, Integer trangthai) {
        if (page < 0) {
            throw new IllegalArgumentException("Chỉ số trang không được nhỏ hơn số không");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return chatLieuRespository.findByTenAndTrangthaiCL(tencl, trangthai, pageable);
    }

}
