package com.example.naturelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;

@EnableJpaRepositories("com.example.naturelink.Repository")
@EntityScan("com.example.naturelink.Entity")
@SpringBootApplication(scanBasePackages = "com.example.naturelink")
public class NatureLinkApplication {

    public static void main(String[] args) {
        // Generate and print JWT secret key
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated Key: " + base64Key);

        SpringApplication.run(NatureLinkApplication.class, args);
    }
}