package com.example.naturelink.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // Changez en fonction de l'URL de votre frontend
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // URL accessible pour les images
                .addResourceLocations("file:./uploads/") // Dossier local où les images sont stockées
                .setCachePeriod(3600); // Cache les images pour 1 heure
    }
}
