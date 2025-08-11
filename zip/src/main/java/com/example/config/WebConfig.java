package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${upload.service.path}")
    private String serviceUploadPath;
    
    @Value("${upload.avatar.path}")
    private String avatarUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình cho service images
        registry.addResourceHandler("/uploads/services/**")
                .addResourceLocations("file:" + serviceUploadPath);
        
        // Cấu hình cho avatar images
        registry.addResourceHandler("/uploads/avatar/**")
                .addResourceLocations("file:" + avatarUploadPath);
    }
}