package com.crypto_tracker_app;

public class ConsumerApp {
    public static void main(String[] args) {
        CryptoPriceConsumer consumer = new CryptoPriceConsumer(); 
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {   
            System.out.println("Shutdown signal received. Closing consumer...");
            consumer.close(); // Graceful shutdown
        }));
        
        System.out.println("Starting Kafka consumer...");
        consumer.startConsuming();                                
    }
}
