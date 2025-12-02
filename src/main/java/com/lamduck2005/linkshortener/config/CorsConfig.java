package com.lamduck2005.linkshortener.config; // Đảm bảo đúng package

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // Cấu hình CORS cho các API
        registry.addMapping("/**")

                // Cho phép domain frontend (Vue) của bạn gọi đến
                .allowedOrigins("*")

                // (Sau này khi deploy, bạn sẽ thêm domain thật vào đây)
                // .allowedOrigins("http://localhost:5173", "https://ten-mien-cua-ban.com")

                // Cho phép các phương thức này
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")

                // Cho phép tất cả các header
                .allowedHeaders("*");
    }
}