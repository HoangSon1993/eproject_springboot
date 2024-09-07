package com.sontung.eproject_springboot.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf(csrf -> {
//            try {
//                csrf.disable().authorizeHttpRequests((authorize) -> authorize
//                                .requestMatchers("/admin/**").hasRole("ADMIN")
//                                .requestMatchers("/**").hasRole("USER")
//                                .requestMatchers("/home").permitAll()
//                                .anyRequest().denyAll())
//                        .formLogin(form -> form
//                                .loginPage("/admin/auth/login")
//                                .loginProcessingUrl("/admin/auth/login")
//                                .defaultSuccessUrl("/admin/auth/home", true)  //phải có tham số thứ 2
//                                .permitAll()
//                        )
//                        .formLogin(form -> form
//                                .loginPage("/auth/login")
//                                .loginProcessingUrl("/auth/login")
//                                .defaultSuccessUrl("/home-page", false)  //phải có tham số thứ 2
//                                .permitAll()
//                        )
//                        .logout(logout -> logout
//                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                                .permitAll()
//                        );
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        return httpSecurity.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Quyền truy cập cho các URL của admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Quyền truy cập cho các URL của user
                        .requestMatchers("/**").hasRole("USER")
                        // Quyền truy cập cho trang /home
                        .requestMatchers("/home-page").permitAll()
                        // Quyền truy cập cho các URL công cộng
                        // Quyền truy cập cho các yêu cầu khác
                        .anyRequest().denyAll()
                )
                .formLogin(form -> form
                        .loginPage("/admin/auth/login") // Trang đăng nhập cho admin
                        .loginProcessingUrl("/admin/auth/login")
                        .defaultSuccessUrl("/admin/auth/home", true)  // Chuyển hướng sau khi đăng nhập thành công
                        .permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login") // Trang đăng nhập cho người dùng
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/home-page", true)  // Chuyển hướng sau khi đăng nhập thành công
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    //    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf(csrf -> {
//            try {
//                csrf.disable().authorizeHttpRequests((authorize) -> authorize.anyRequest()
//                                .authenticated()).formLogin(form ->
//                                form.loginPage("/admin/auth/login")
//                                        .loginProcessingUrl("/admin/auth/login")
//                                        .defaultSuccessUrl("/admin/auth/home")
//                                        .permitAll()).logout(
//                                logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                                        .permitAll());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        return httpSecurity.build();
//    }
}
