package com.sontung.eproject_springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để Spring Boot tìm kiếm tài nguyên tĩnh
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("/user_assets/**")
                .addResourceLocations("classpath:/static/user_assets/");

        registry.addResourceHandler("/demo/**")
                .addResourceLocations("classpath:/static/demo/");
    }
}
