package com.example.demo.baove.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class sss{

//    public static void main(String[] args) {
////        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
////
////        String encodedPassword = passwordEncoder.encode("123456");
////        System.out.println(encodedPassword); // In ra mật khẩu đã mã hóa
//
//
//    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.out.println("Access Key: " + dotenv.get("CLOUDFLARE_ACCESS_KEY"));
    }

}




