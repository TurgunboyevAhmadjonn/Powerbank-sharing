// StationServiceApplication.java
package com.anor.station;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class StationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StationServiceApplication.class, args);
    }
}
