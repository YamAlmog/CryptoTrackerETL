package com.crypto_tracker_app;
import java.util.logging.Logger;

public class ConsumerApp {
    private static final Logger logger = Logger.getLogger(CryptoMarketDataConsumer.class.getName());

    public static void main(String[] args) {
        CryptoMarketDataConsumer consumer = new CryptoMarketDataConsumer(); 
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {   
            logger.info("Shutdown signal received. Closing consumer...");
            consumer.close(); // Graceful shutdown
        }));
        
        logger.info("Starting Kafka consumer...");
        consumer.startConsuming();                                
    }
}
