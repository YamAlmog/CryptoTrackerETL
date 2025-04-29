package com.crypto_tracker_app;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TokenMetadataApiApp {

    private static final Logger logger = Logger.getLogger(TokenMetadataApiApp.class.getName());

    public static void main(String[] args) { 
        logger.info("------------> Inside main TokenMetadataApiApp");
        SpringApplication.run(TokenMetadataApiApp.class, args);
    }
}
