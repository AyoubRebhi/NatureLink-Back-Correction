package com.example.naturelink.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ✅ Inject the dynamic frontend URL from application.properties
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Serve files from /uploads
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        // ✅ Serve files from /static
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:static/");
    }


}
