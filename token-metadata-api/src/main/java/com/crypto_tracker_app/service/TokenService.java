package com.crypto_tracker_app.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.crypto_tracker_app.Coin;
import com.crypto_tracker_app.CoinStorageManager;
import com.crypto_tracker_app.exception.CoinNotFoundException;

@Service
public class TokenService {

    private final CoinStorageManager CoinStorageManager;

    public TokenService() {
        this.CoinStorageManager = new CoinStorageManager();
    }

    public Coin getLatestTokenPriceBySymbol(String symbol) {
        try {
            System.out.println("---------> inside getLatestTokenBySymbol function");
            Coin coin = CoinStorageManager.getLatestPriceBySymbol(symbol);
            if (coin == null) {
                throw new CoinNotFoundException(symbol);
            }
            return coin;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred");
        }
    }

    public Coin getHighestTokenPriceBySymbol(String symbol) {
        try {
            System.out.println("---------> inside getLatestTokenBySymbol function");
            Coin coin = CoinStorageManager.getHighestPriceBySymbol(symbol);
            if (coin == null) {
                throw new CoinNotFoundException(symbol);
            }
            return coin;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred");
        }
    }
}

