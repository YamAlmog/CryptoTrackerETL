package com.crypto_tracker_app;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProducerApp {
    public static int SCHEDUAL_FIXED_RATE = 30;
    public static int TOTAL_PAGES = 5;

    public static void main(String[] args) {
        CryptoMarketDataFetcher fetcher = new CryptoMarketDataFetcher();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable fetchTask = () -> {
            try {
                fetcher.getCryptoMetadata(TOTAL_PAGES);
            } catch (Exception e) {
                System.err.println("Unexpected error:");
                e.printStackTrace();
            }
        };

        System.out.println("Starting scheduled crypto price fetcher...");
        scheduler.scheduleAtFixedRate(fetchTask, 0, SCHEDUAL_FIXED_RATE, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown signal received. Closing resources...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }

            fetcher.close();
            System.out.println("Fetcher closed. Exiting producer app.");
        }));
    }
}