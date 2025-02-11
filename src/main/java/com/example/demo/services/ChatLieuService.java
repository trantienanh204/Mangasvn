package com.example.demo.services;

import com.example.demo.entity.ChatLieu;

import java.util.List;

public interface ChatLieuService {

    List<ChatLieu> list();
    void add(ChatLieu chatLieu);
    void update(ChatLieu chatLieu);

}
