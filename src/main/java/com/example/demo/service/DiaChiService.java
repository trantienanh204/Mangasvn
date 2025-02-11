package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class DiaChiService {
    private final RestTemplate restTemplate ;
    public DiaChiService (RestTemplate restTemplate){
        this.restTemplate = restTemplate ;
    }

    public String getTinh(){
        String url = "https://vapi.vnappmob.com/api/province/";
        return restTemplate.getForObject(url, String.class);
    }

    public String getHuyen(String tinhId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://vapi.vnappmob.com/api/province/district")
                .pathSegment(tinhId)
                .toUriString();
        return restTemplate.getForObject(url, String.class);
    }

    public String getPhuong(String huyenId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://vapi.vnappmob.com/api/province/ward")
                .pathSegment(huyenId)
                .toUriString();
        return restTemplate.getForObject(url, String.class);
    }
}
