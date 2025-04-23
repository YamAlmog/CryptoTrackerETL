package com.crypto_tracker_app.exception;

public class CoinNotFoundException extends RuntimeException {

    public CoinNotFoundException(String symbol) {
        super("Coin with symbol '" + symbol + "' not found.");
    }
}