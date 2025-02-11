package com.example.demo.Controller.admin.BanHang;

import com.example.demo.entity.DiaChi;
import com.example.demo.entity.QuanHuyen;
import com.example.demo.entity.Tinh_TP;
import com.example.demo.entity.XaPhuong;
import com.example.demo.respository.QuanHuyenRepo;
import com.example.demo.respository.TinhTPRepo;
import com.example.demo.respository.XaPhuongRepo;
import com.example.demo.service.DiaChiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DiaChiController {
    @Autowired
    private TinhTPRepo provinceRepository;

    @Autowired
    private QuanHuyenRepo districtRepository;

    @Autowired
    private XaPhuongRepo wardRepository;

    @GetMapping("/provinces")
    public List<Tinh_TP> getProvinces() {
        return provinceRepository.findAll();
    }

    @GetMapping("/districts")
    public List<QuanHuyen> getDistricts(@RequestParam int provinceId) {
        return districtRepository.findByProvinceId(provinceId);
    }

    @GetMapping("/wards")
    public List<XaPhuong> getWards(@RequestParam int districtId) {
        return wardRepository.findByDistrictId(districtId);
    }
}


