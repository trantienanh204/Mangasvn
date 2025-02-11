package com.example.demo.services.impl;

import com.example.demo.entity.ChatLieu;
import com.example.demo.respository.ChatLieuRespository;
import com.example.demo.services.ChatLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatLieuServiceimpl implements ChatLieuService {
    @Autowired
    ChatLieuRespository chatLieuRespository;

    @Override
    public List<ChatLieu> list() {
        return chatLieuRespository.findAll();
    }

    @Override
    public void add(ChatLieu chatLieu) {
        chatLieuRespository.save(chatLieu);
    }

    @Override
    public void update(ChatLieu chatLieu) {
        chatLieuRespository.save(chatLieu);
    }

}
