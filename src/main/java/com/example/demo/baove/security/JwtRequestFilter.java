package com.example.demo.baove.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header hiện tại là : " + authorizationHeader);
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
                System.out.println("Received JWT: " + jwt);
                System.out.println("Extracted Username: " + username);
                String role = jwtUtil.getRoleFromToken(jwt);
                System.out.println("Extracted Role: " + role);
            } catch (IllegalArgumentException e) {
                System.out.println("Không thể lấy JWT Token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token đã hết hạn: " + e.getMessage());
            } catch (MalformedJwtException | SignatureException e) {
                System.out.println("JWT Token không hợp lệ: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt)) {
                String role = jwtUtil.getRoleFromToken(jwt);
                System.out.println("Validated Role from Token: " + role);

                // Sử dụng vai trò từ token thay vì từ UserDetailsService
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                System.out.println("Authorities set: " + authorities);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("✅ Authentication sau khi đặt: " + SecurityContextHolder.getContext().getAuthentication());
            } else {
                System.out.println("❌ Token không hợp lệ, không đặt Authentication");
            }
        } else {
            System.out.println("❌ Không thể đặt Authentication: username=" + username + ", existing auth=" + SecurityContextHolder.getContext().getAuthentication());
        }

        chain.doFilter(request, response);
    }
}