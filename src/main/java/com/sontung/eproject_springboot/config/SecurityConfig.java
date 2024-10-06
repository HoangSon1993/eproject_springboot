package com.sontung.eproject_springboot.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /**
     * @Summary: Sử dụng thuật toán Bcrypt để̉ mã hoá mật khẩu
     * @Description: Độ mạnh của mật khẩu là 10.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    private final String[] ADMIN_PUBLIC_ENDPOINT_POST = {"/admin/auth/login"};
    private final String[] ADMIN_PUBLIC_ENDPOINT_GET = {};
    private final String[] ADMIN_PRIVATE_ENDPOINT_POST = {

    };
    private final String[] ADMIN_PRIVATE_ENDPOINT_GET = {
            "/admin/category/index",
            "/admin/category/detail",
            "/admin/category/create",
            "/admin/category/edit",

            "/admin/combo",
            "/admin/combo/detail",
            "/admin/combo/create",
            "/admin/combo/expired",
            "/admin/home"
    };


    @Bean
    @Order(1) //Quy định thứ tự ưu tiên của filter chain này. Filter chain cho admin được xử lý trước (ưu tiên số 1).
    public SecurityFilterChain adminFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.securityMatcher("/admin/**") //Chỉ áp dụng filter chain này cho các URL bắt đầu với /admin/.
                .csrf(AbstractHttpConfigurer::disable) //Tắt tính năng bảo vệ CSRF (Cross-Site Request Forgery) để tránh lỗi cho các yêu cầu POST/PUT/DELETE từ form không có CSRF token.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/assets/**", "/user_assets/**", "/demo/**").permitAll() // Các tài nguyên tĩnh cũng được mở quyền truy cập cho tất cả.
                        .requestMatchers("/admin/auth/**").permitAll()//Mở quyền truy cập cho tất cả (đăng nhập, đăng ký không yêu cầu bảo mật).
                        .requestMatchers("/admin/**").hasRole("ADMIN")//Chỉ những người dùng có vai trò ADMIN mới có quyền truy cập.
                        //.requestMatchers("/admin/auth/**").permitAll()
                        //.requestMatchers(HttpMethod.GET, ADMIN_PRIVATE_ENDPOINT_GET).hasRole("ADMIN")
                        .anyRequest().denyAll() //Mọi yêu cầu khác đều bị từ chối.
                ).formLogin(form -> form.loginPage("/admin/auth/login") // Trang đăng nhập cho admin
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler()) // Ghi nhớ URL trước đó, sau khi đăng nhập thành công thì Redireact tới URL này.
                        .defaultSuccessUrl("/admin/home", false)  // Chuyển hướng sau khi đăng nhập thành công
                        //.successHandler(customAuthenticationSuccessHandler)
                        .permitAll())
                .logout(logout -> logout //Định cấu hình cho tính năng đăng xuất
                        .logoutUrl("/admin/logout") // Xử lý logout tại URL này
                        .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout", "POST")) // Chỉ cho phép phương thức POST
                        .logoutSuccessUrl("/admin/auth/login") // Chuyển hướng sau khi logout thành công
                        .permitAll());
        return httpSecurity.build();
    }


    private final String[] PUBLIC_ENDPOINT_POST = {"/user/registerConfirm", "/user/auth/login"};
    private final String[] PUBLIC_ENDPOINT_GET = {"/home-page", "/product/**", "/combo/**", "/user/auth/login", "/user/register"};
    private final String[] PRIVATE_ENDPOINT_POST = {"/user/edit-info-confirm", "/change-password-confirm", "/cart/remove"};
    private final String[] PRIVATE_ENDPOINT_GET = {"/order/index", "/order/**", "/cart/**", "/user/my-info", "/user/edit-info", "/user/change-password"};

    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity httpSecurity, CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/assets/**", "/user_assets/**", "/demo/**").permitAll() // Tài nguyên tĩnh được phép truy cập công khai.
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).not().hasRole("ADMIN").requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).not().hasRole("ADMIN")
                        //Mở quyền truy cập cho tất cả người dùng.
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll().requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
                        //Chỉ những người dùng có vai trò USER mới được truy cập.
                        .requestMatchers(PRIVATE_ENDPOINT_GET).hasRole("USER").requestMatchers(PRIVATE_ENDPOINT_POST).hasRole("USER") // Loại bỏ khỏi quản lý bằng cách cho phép tất cả
                        .anyRequest().denyAll() //Mọi yêu cầu khác đều bị từ chối.
                ).formLogin(form -> form.loginPage("/user/auth/login") // Trang đăng nhập cho người dùng
                        .loginProcessingUrl("/user/auth/login") // URL xử lý đăng nhập.
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler()) // Ghi nhớ URL trước đó, sau khi đăng nhập thành công thì Redireact tới URL này.
                        .defaultSuccessUrl("/home-page", false)  //Default: Chuyển hướng sau khi đăng nhập thành công
                        .permitAll()).logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) //Chỉ cho phép đăng xuất bằng phương thức POST.
                        .permitAll())
                // Xử lý ngoại lệ nếu user có ROLE_ADMIN vào những endpoint không được vào.
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler));
        return httpSecurity.build();
    }

    /**
     * @Summary: Trong Spring Security là thành phần chính chịu trách nhiệm xác thực (authentication) các yêu cầu đăng nhập của người dùng.
     * Nó kiểm tra thông tin đăng nhập (như tên người dùng và mật khẩu) và quyết định xem người dùng có được cấp quyền truy cập hay không.
     * @Description: Nhận thông tin đăng nhập: Khi người dùng gửi yêu cầu đăng nhập, AuthenticationManager nhận thông tin này dưới dạng đối tượng Authentication (chứa tên người dùng và mật khẩu).
     * Xác thực thông tin: AuthenticationManager kiểm tra thông tin đăng nhập dựa trên cấu hình bảo mật của ứng dụng (như kiểm tra mật khẩu với cơ sở dữ liệu hoặc các dịch vụ xác thực khác như LDAP, OAuth).
     * Cấp quyền hoặc từ chối: Nếu thông tin xác thực đúng, AuthenticationManager sẽ cấp quyền cho người dùng và lưu trữ trạng thái đăng nhập. Nếu thông tin không hợp lệ, nó sẽ từ chối quyền truy cập.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Note: Thứ tự cấu hình trong security:
     * 1. Cấu hình các tài nguyên tĩnh (css, js, image, ...). Các tài nguyên này không cần được bảo vệ nên được cấu hình đầu tiền với permitAll().
     * 2. Các endpoint công khai (đăng nhập, đăng ký, trang chủ,..).
     * Các trang như đăng nhập, đăng ký, hoặc trang chủ có thể được mở rộng quyền truy cập cho tất cả người dùng, do đó nên được đặt ngay sau tài nguyên tĩnh.
     * 3. Các endpoint cần bảo vệ: Sau khi xác định các endpoint công khai, bạn cần bảo vệ các phần còn lại, như các trang yêu cầu quyền truy cập của người dùng đã đăng nhập (hasRole(), hasAuthority(), v.v.).
     * 4. Mọi yêu cầu khác: Cuối cùng, để bảo vệ tất cả các yêu cầu chưa được chỉ định rõ ràng, bạn có thể sử dụng .anyRequest().denyAll() hoặc .anyRequest().authenticated().
     * */
}