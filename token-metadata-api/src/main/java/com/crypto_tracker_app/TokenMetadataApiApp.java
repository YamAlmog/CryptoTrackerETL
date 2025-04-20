package com.crypto_tracker_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TokenMetadataApiApp {
    public static void main(String[] args) { 
        System.out.println("------------> Inside main TokenMetadataApiApp");
        SpringApplication.run(TokenMetadataApiApp.class, args);
    }
}
