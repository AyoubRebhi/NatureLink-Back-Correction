package com.example.naturelink.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Covers all endpoints
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true);

        // If you want to restrict CORS for static files separately, you can uncomment this:
        /*
        registry.addMapping("/static/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET");
        */
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/menus/uploads/**")
                .addResourceLocations("file:./Uploads/menus/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./Uploads/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:static/");
    }
}
