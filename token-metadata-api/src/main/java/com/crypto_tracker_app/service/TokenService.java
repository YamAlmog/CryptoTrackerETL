package com.crypto_tracker_app.service;

import com.crypto_tracker_app.Coin;
import com.crypto_tracker_app.CoinStorageManager;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final CoinStorageManager CoinStorageManager;

    public TokenService() {
        this.CoinStorageManager = new CoinStorageManager();
    }

    public Coin getLatestTokenBySymbol(String symbol) {
        try {
            System.out.println("---------> inside getLatestTokenBySymbol function");
            return CoinStorageManager.getLatestCoinBySymbol(symbol);
        } catch (Exception e) {
            System.out.println("---------> Error with getLatestTokenBySymbol <----------");
            e.printStackTrace();
            return null;
        }
    }

    public Coin getHighestTokenBySymbol(String symbol) {
        try {
            System.out.println("---------> inside getHighestTokenBySymbol function");
            return CoinStorageManager.getHighestCoinBySymbol(symbol);
        } catch (Exception e) {
            System.out.println("---------> Error with getHighestTokenBySymbol <----------");
            e.printStackTrace();
            return null;
        }
    }
}
