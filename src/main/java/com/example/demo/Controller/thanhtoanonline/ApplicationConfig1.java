package com.example.demo.Controller.thanhtoanonline;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig1 {

    @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
}
