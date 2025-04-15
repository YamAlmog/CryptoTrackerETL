package com.stock_tracker_app;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        List<String> COINS = Arrays.asList("bitcoin", "ethereum", "cardano", "solana");
        CryptoPriceFetcher fetcher = new CryptoPriceFetcher();

        System.out.println("Starting scheduled crypto price fetcher...");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable fetchTask = () -> {
            try {
                fetcher.getCryptoPrices(COINS);
            } catch (IOException e) {
                System.err.println("IOException during fetch:");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Fetch interrupted:");
                Thread.currentThread().interrupt(); // good practice
            }
        };

        // Schedule the task to run every 20 seconds, with no initial delay
        scheduler.scheduleAtFixedRate(fetchTask, 0, 20, TimeUnit.SECONDS);

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
