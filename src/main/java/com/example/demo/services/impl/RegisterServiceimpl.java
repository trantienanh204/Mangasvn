package com.example.demo.services.impl;

import com.example.demo.entity.KhachHang;
import com.example.demo.respository.RegisterRespository;
import com.example.demo.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceimpl implements RegisterService {
    @Autowired
    RegisterRespository resgisterRespository;
    @Override
    public void createAcount(KhachHang khachHang) {
        resgisterRespository.save(khachHang);
    }
}
