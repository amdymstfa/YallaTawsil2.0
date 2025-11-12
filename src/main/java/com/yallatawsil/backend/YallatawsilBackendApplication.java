package com.yallatawsil.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yallatawsil.backend")

public class YallatawsilBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(YallatawsilBackendApplication.class, args);
    }
}
