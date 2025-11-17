package com.malaka.aat.internal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.file.path}")
    private String imagePath;

    @Value("${app.file.video-path}")
    private String videoPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve images publicly from /uploads/images/**
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + imagePath);

        // Serve videos publicly from /uploads/videos/**
        registry.addResourceHandler("/uploads/videos/**")
                .addResourceLocations("file:" + videoPath);
    }
}
