package com.example.demo.service;


import com.example.demo.entity.HoaDonCT;
import com.example.demo.respository.HoaDonCTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HDCTService {
    @Autowired
    HoaDonCTRepository hoaDonCTRepository;
    public List<HoaDonCT> findID(Integer id) {
        return hoaDonCTRepository.findhdct(id);
    }

}
