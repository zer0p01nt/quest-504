package com.example.quest504;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Quest504Application {

    public static void main(String[] args) {
        SpringApplication.run(Quest504Application.class, args);
    }
}
