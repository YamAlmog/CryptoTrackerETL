package com.stock_tracker_app;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static int SCHEDUAL_FIXED_RATE = 30;
    public static int TOTAL_PAGES = 5;

    public static void main(String[] args) {
        CryptoPriceFetcher fetcher = new CryptoPriceFetcher();
        CryptoPriceConsumer consumer = new CryptoPriceConsumer();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable fetchTask = () -> {
            try {
                fetcher.getCryptoPrices(TOTAL_PAGES);
            } catch (IOException e) {
                System.err.println("IOException during fetch:");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Fetch interrupted:");
                Thread.currentThread().interrupt(); // good practice
            }
        };
        System.out.println("Starting consumer thread...");
        // Start the consumer in a background thread
        Thread consumerThread = new Thread(consumer::startConsuming);
        consumerThread.start();
        
        System.out.println("Starting scheduled crypto price fetcher...");
        // Schedule the task to run every SCHEDUAL_FIXED_RATE seconds, with no initial delay
        scheduler.scheduleAtFixedRate(fetchTask, 0, SCHEDUAL_FIXED_RATE, TimeUnit.SECONDS);

        // Graceful shutdown hook
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
            System.out.println("Fetcher closed. Exiting app.");
        }));
    }
}
