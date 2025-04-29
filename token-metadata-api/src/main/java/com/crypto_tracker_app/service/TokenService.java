package com.crypto_tracker_app.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.crypto_tracker_app.Coin;
import com.crypto_tracker_app.CoinStorageManager;
import com.crypto_tracker_app.exception.CoinNotFoundException;
@Service
public class TokenService {

    private static final Logger logger = Logger.getLogger(TokenService.class.getName());
    private final CoinStorageManager coinStorageManager;

    public TokenService() {
        this.coinStorageManager = new CoinStorageManager();
    }

    public List<String> getAllTokenSymbols() {
        try {
            logger.info("Fetching all token symbols...");
            List<String> symbols = coinStorageManager.getAllTokenSymbols();
            if (symbols == null) {
                logger.severe("Symbols are null, throwing SQLException");
                throw new SQLException();
            }
            return symbols;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error occurred while retrieving symbols", e);
            throw new RuntimeException();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred while retrieving symbols", e);
            throw new RuntimeException();
        }
    }


    public List<String> getAllTokenIds() {
        try {
            logger.info("Fetching all token ids...");
            List<String> ids = coinStorageManager.getAllTokenIds();
            if (ids == null) {
                logger.severe("Ids are null, throwing SQLException");
                throw new SQLException();
            }
            return ids;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error occurred while retrieving ids", e);
            throw new RuntimeException();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred while retrieving ids", e);
            throw new RuntimeException();
        }
    }

    public Coin getLatestTokenPriceBySymbol(String symbol) {
        try {
            logger.log(Level.INFO, "Fetching latest record for token: ", symbol);
            Coin coin = coinStorageManager.getLatestPriceBySymbol(symbol);
            if (coin == null) {
                throw new CoinNotFoundException(symbol);
            }
            return coin;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error occurred while fetching latest price for symbol: "+ symbol, e);
            throw new RuntimeException();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred while fetching latest price for symbol: "+ symbol, e);
            throw new RuntimeException();
        }
    }

    public Coin getHighestTokenPriceBySymbol(String symbol) {
        try {
            logger.log(Level.INFO, "Fetching highest price record for token: ", symbol);
            Coin coin = coinStorageManager.getHighestPriceBySymbol(symbol);
            if (coin == null) {
                throw new CoinNotFoundException(symbol);
            }
            return coin;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Database error occurred while fetching highest price for symbol: "+ symbol, e);
            throw new RuntimeException();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred while fetching highest price for symbol: "+ symbol, e);
            throw new RuntimeException();
        }
    }
}

