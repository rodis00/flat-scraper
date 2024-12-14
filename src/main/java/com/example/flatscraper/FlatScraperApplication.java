package com.example.flatscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlatScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlatScraperApplication.class, args);
    }

}
