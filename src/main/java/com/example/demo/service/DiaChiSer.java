package com.example.demo.service;

import com.example.demo.entity.DiaChi;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DiaChiSer {
    private RestTemplate restTemplate;

    public DiaChiSer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DiaChi> getAllProvinces() {
        String url = "https://vietnamprovinces.info/api/provinces";
        DiaChi[] provinces = restTemplate.getForObject(url, DiaChi[].class);
        return List.of(provinces);
    }
}
