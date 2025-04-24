package com.crypto_tracker_app.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crypto_tracker_app.Coin;
import com.crypto_tracker_app.CoinStorageManager;
import com.crypto_tracker_app.exception.CoinNotFoundException;

@Service
public class TokenService {

    private final CoinStorageManager coinStorageManager;

    public TokenService() {
        this.coinStorageManager = new CoinStorageManager();
    }

    public List<String> getAllTokenSymbols() {
        try {
            List<String> symbols = coinStorageManager.getAllTokenSymbols();
            if (symbols == null) {
                throw new SQLException();
            }
            return symbols;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error");
        }
    }

    public Coin getLatestTokenPriceBySymbol(String symbol) {
        try {
            System.out.println("---------> inside getLatestTokenBySymbol function");
            Coin coin = coinStorageManager.getLatestPriceBySymbol(symbol);
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
            Coin coin = coinStorageManager.getHighestPriceBySymbol(symbol);
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

