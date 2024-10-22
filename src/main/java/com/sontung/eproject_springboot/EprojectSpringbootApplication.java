package com.sontung.eproject_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EprojectSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(EprojectSpringbootApplication.class, args);
    }
}
