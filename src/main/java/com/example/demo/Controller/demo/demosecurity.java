package com.example.demo.Controller.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class demosecurity {
//    // mã hóa mật khẩu
//    @Bean
//    public PasswordEncoder getPasswordEncoder2(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        PasswordEncoder  pe = getPasswordEncoder2();
//        UserDetails user1 = User.withUsername("user")
//                .password(pe.encode("123"))
//                .roles("GUES")
//                .build();
//
//        UserDetails user2 = User.withUsername("user2")
//                .password(pe.encode("123"))
//                .roles("ADMIN","GUES")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
//        http.csrf().disable().cors().disable();
//
//        http.authorizeHttpRequests(requests -> requests
//                        .requestMatchers( "/panda/login","/auth/login","/auth/account","/Image/**").permitAll()
//                        .requestMatchers("/panda/index/**").hasAnyRole("NHANVIEN")
//                        .requestMatchers("/panda/vaitro/**").hasAnyRole("QUANLY")
//                        //.anyRequest().authenticated() // các url còn lại phải đăng nhập để sử dụng
//                        .anyRequest().permitAll() // các url còn lại không cần đăng nhập vẫn có thể sử dụng
//                );
//        http.exceptionHandling().accessDeniedPage("/"); // url khi đăng nhập trái phép
//        http.formLogin(form -> form
//                .loginPage("/auth/login")  // Đường dẫn đến trang đăng nhập tùy chỉnh
//                .loginProcessingUrl("/auth/account")  // URL xử lý đăng nhập
//                .defaultSuccessUrl("/home/index", false)  // Trang chuyển đến sau khi đăng nhập thành công
//                .failureUrl("/auth/account")  // Trang chuyển đến khi đăng nhập thất bại
//                .usernameParameter("username")  // Tên tham số cho trường username
//                .passwordParameter("password")  // Tên tham số cho trường password
//        );
////        http
////                .rememberMe()  // Kích hoạt tính năng "Remember Me"
////                .key("uniqueAndSecret")
////                .rememberMeParameter("remember"); // Tên của trường "Remember Me" trong form login
//        // Cấu hình logout
//        http.logout(logout -> logout
//                .logoutUrl("/auth/logout")  // URL để thực hiện logout
//                .logoutSuccessUrl("/auth/account")  // Trang chuyển đến sau khi logout thành công
//                .addLogoutHandler(new SecurityContextLogoutHandler())  // Thêm handler cho logout
//        );
//        return http.build();
//    }
}
