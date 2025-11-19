package com.supera.accessrequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AccessRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessRequestApplication.class, args);
    }
}

