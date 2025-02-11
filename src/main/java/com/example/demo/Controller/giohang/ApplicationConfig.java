package com.example.demo.Controller.giohang;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate customRestTemplate() {
        return new RestTemplate();
    }
}

