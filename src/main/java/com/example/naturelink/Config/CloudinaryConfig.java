package com.example.naturelink.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dueynqcy4");
        config.put("api_key", "541739413453923");
        config.put("api_secret", "aYAM2rDPSQhWuxWsGY8hk_-93Q0");
        return new Cloudinary(config);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}