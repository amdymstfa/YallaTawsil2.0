package com.yallatawsil.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = {})

public class YallatawsilBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(YallatawsilBackendApplication.class, args);
        System.out.println("\n=========================================================\n" +
                " Yalla Tawsil Backend - Delivery Route Optimization System\n" +
                "=========================================================\n");
    }
}
