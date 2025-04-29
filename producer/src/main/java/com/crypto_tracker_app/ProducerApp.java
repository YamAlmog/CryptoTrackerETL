package com.crypto_tracker_app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProducerApp {
    public static final int SCHEDUAL_FIXED_RATE = 60;
    public static final int TOTAL_PAGES = 5;
    private static final Logger logger = Logger.getLogger(ProducerApp.class.getName());

    public static void main(String[] args) {
        CryptoMarketDataFetcher fetcher = new CryptoMarketDataFetcher();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable fetchTask = () -> {
            try {
                fetcher.getCryptoCoinsInfo(TOTAL_PAGES);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unexpected error: ", e);
            }
        };

        logger.info("Starting scheduled crypto price fetcher...");
        scheduler.scheduleAtFixedRate(fetchTask, 0, SCHEDUAL_FIXED_RATE, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Closing resources...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }

            fetcher.close();
            logger.info("Fetcher closed. Exiting producer app.");
        }));
    }
}