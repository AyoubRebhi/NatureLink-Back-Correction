package com.example.naturelink.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Gestion du dossier /uploads avec chemin absolu
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        // Gestion du dossier /static
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/static/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET");


    }
}
