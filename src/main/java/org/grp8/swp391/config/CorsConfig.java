package org.grp8.swp391.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // ✅ Ghi rõ origin FE (Next.js)
                        .allowedOrigins("http://localhost:3000")
                        // ✅ Cho phép các method cần thiết
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        // ✅ Cho phép mọi header
                        .allowedHeaders("*")
                        // ✅ Cho phép gửi token, cookies, v.v.
                        .allowCredentials(true);
            }
        };
    }
}
