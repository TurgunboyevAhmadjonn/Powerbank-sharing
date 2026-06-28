// src/main/java/com/anor/rental/RentalServiceApplication.java
package com.anor.rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RentalServiceApplication.class, args);
    }
}
