package com.example.demo.config;

import com.example.demo.filters.JwtTokenFilter;
import com.example.demo.Service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Tạm thời dùng NoOpPasswordEncoder để so sánh mật khẩu thô từ database khi test
    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // NƠI CẤU HÌNH PHÂN QUYỀN ĐƯỜNG DẪN
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt bảo vệ CSRF vì sử dụng API REST với Token
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không sử dụng Session lưu trữ trên server
            .authorizeHttpRequests(auth -> auth
                // 1. Các API cho phép mọi người truy cập công khai (Không cần đăng nhập)
                .requestMatchers("/","/court" ,"/index.html", "/static/**", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/court", "/api/court/**").permitAll() // Ai cũng được xem danh sách sân

                // 2. Phân quyền cho CHỦ SÂN (COURT_OWNER) và ADMIN
                .requestMatchers(HttpMethod.POST, "/api/court/**").hasAnyRole("COURT_OWNER", "ADMIN")
                .requestMatchers("/court/booking/approve/**", "/court/booking/cancel/**").hasAnyRole("COURT_OWNER", "ADMIN")

                // 3. Phân quyền riêng cho ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 4. Các yêu cầu còn lại bắt buộc phải đăng nhập
                .anyRequest().authenticated()
            );

        // Đăng ký bộ lọc JWT trước khi kiểm tra Username/Password của Spring Security
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
