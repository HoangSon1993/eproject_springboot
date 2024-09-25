package com.sontung.eproject_springboot.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/admin/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/auth/**").permitAll()
                        .requestMatchers("/assets/**", "/user_assets/**", "/demo/**").permitAll()
                        //.requestMatchers("/admin/auth/**").permitAll()
                        .anyRequest().denyAll()
                )
                .formLogin(form -> form
                        .loginPage("/admin/auth/login") // Trang đăng nhập cho admin
                        .defaultSuccessUrl("/admin/auth/home", true)  // Chuyển hướng sau khi đăng nhập thành công
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout") // Xử lý logout tại URL này
                        .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout", "POST")) // Chỉ cho phép phương thức POST
                        .logoutSuccessUrl("/admin/auth/login") // Chuyển hướng sau khi logout thành công
                        .permitAll()
                );
        return httpSecurity.build();
    }
    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/auth/**","/home-page","/user/register","/**").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/assets/**","/user_assets/**", "/demo/**").permitAll()
                        .requestMatchers("/user/**").not().hasRole("ADMIN")
                        .anyRequest().denyAll()
                )
                .formLogin(form -> form
                        .loginPage("/user/auth/login") // Trang đăng nhập cho người dùng
                        .loginProcessingUrl("/user/auth/login")
                        .defaultSuccessUrl("/home-page", true)  // Chuyển hướng sau khi đăng nhập thành công
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .permitAll()
                );
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
