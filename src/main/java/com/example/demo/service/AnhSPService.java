package com.example.demo.service;

import com.example.demo.entity.Anh_SP;
import com.example.demo.respository.AnhSPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnhSPService {

    @Autowired
    private AnhSPRepository anhSPRepository;

    public Anh_SP save(Anh_SP anhSP) {
        return anhSPRepository.save(anhSP);
    }
}

