package com.example.demo.baove.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/view/**", "/favicon.ico", "/error", "/truyen/**", "/trangchu/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/user-info").permitAll()
                        .requestMatchers("/api/categories", "/api/authors", "/api/chapters", "/api/images", "/api/comments/chapter/**", "/api/comments/comic/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/truyen/favorite/list", "/api/truyen/favorite/status/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/truyen/favorite/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/truyen/favorite/**").authenticated()
                        .requestMatchers("/api/truyen/authors").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers("/api/truyen").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers("/api/truyen/*/chapters").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/truyen/authors").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/truyen/**").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/truyen/**").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/truyen/**").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/truyen/hot", "/api/truyen/moi", "/api/truyen/list", "/api/truyen/listloc", "/api/truyen/search", "/api/truyen/*").permitAll()
                        .requestMatchers("/api/admin/**", "/admin/**").hasAnyRole("ADMIN", "CHUTUT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/comments/add", "/api/comments/*/like").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/comments/**").permitAll()
                        .requestMatchers("/api/truyen/**", "/api/comments/**", "/api/history/**", "/api/auth/**").authenticated()
                        .requestMatchers("/theloai/**", "/search/**", "/fill/**", "/read/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reports").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "https://mangasvn.click",
                "https://www.mangasvn.click",
                "https://mangasvn.onrender.com",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
