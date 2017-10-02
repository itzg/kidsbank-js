package me.itzg.kidsbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class KidsbankApp {

    public static void main(String[] args) {
        SpringApplication.run(KidsbankApp.class, args);
    }

    @Bean
    public Random rand() {
        return new Random();
    }
}
