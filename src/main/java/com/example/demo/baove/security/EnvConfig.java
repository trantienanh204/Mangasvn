//package com.example.demo.baove.security;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class EnvConfig {
//    private static final Dotenv dotenv = Dotenv.load();
//
//    @Bean
//    public String accessKey() {
//        return dotenv.get("CLOUDFLARE_ACCESS_KEY");
//    }
//
//    @Bean
//    public String secretKey() {
//        return dotenv.get("CLOUDFLARE_SECRET_KEY");
//    }
//
//    @Bean
//    public String bucketName() {
//        return dotenv.get("CLOUDFLARE_BUCKET_NAME");
//    }
//    @Bean
//    public String uploadEndpoint() {
//        return dotenv.get("CLOUDFLARE_UPLOAD_ENDPOINT");
//    }
//    @Bean
//    public String publicUrl() {
//        return dotenv.get("CLOUDFLARE_PUBLIC_URL");
//    }
//    @Bean
//    public String secret() {
//        return dotenv.get("JWT_SECRET_KEY");
//    }
//    @Bean
//    public String datasourceUrl() {
//        return dotenv.get("datasource_url");
//    }
//    @Bean
//    public String datasourceUsername() {
//        return dotenv.get("datasource_username");
//    }
//    @Bean
//    public String datasourcePassword() {
//        return dotenv.get("datasource_password");
//    }
//    @Bean
//    public String datasourceDriver() {
//        return dotenv.get("datasource_driver-class-name");
//    }
//}
//
//
