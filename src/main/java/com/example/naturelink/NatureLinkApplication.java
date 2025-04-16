package com.example.naturelink;

import com.example.naturelink.Entity.Restaurant;
import com.example.naturelink.Repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories

public class NatureLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(NatureLinkApplication.class, args);
    }

}
