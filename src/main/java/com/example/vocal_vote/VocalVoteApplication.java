package com.example.vocal_vote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VocalVoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(VocalVoteApplication.class, args);
    }

}
