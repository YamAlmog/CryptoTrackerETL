package com.crypto_tracker_app.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.crypto_tracker_app.Coin;
import com.crypto_tracker_app.CoinStorageManager;
import com.crypto_tracker_app.exception.CoinNotFoundException;
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final CoinStorageManager coinStorageManager;

    public TokenService() {
        this.coinStorageManager = new CoinStorageManager();
    }

    public List<String> getAllTokenSymbols() {
        try {
            logger.info("Fetching all token symbols...");
            List<String> symbols = coinStorageManager.getAllTokenSymbols();
            if (symbols == null) {
                logger.error("Symbols are null, throwing SQLException");
                throw new SQLException();
            }
            return symbols;
        } catch (SQLException e) {
            logger.error("SQL error occurred while retrieving symbols", e);
            throw new RuntimeException("Failed to retrieve Symbols");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving symbols", e);
            throw new RuntimeException("Unexpected Error");
        }
    }


    public List<String> getAllTokenIds() {
        try {
            logger.info("Fetching all token ids...");
            List<String> ids = coinStorageManager.getAllTokenIds();
            if (ids == null) {
                logger.error("Ids are null, throwing SQLException");
                throw new SQLException();
            }
            return ids;
        } catch (SQLException e) {
            logger.error("SQL error occurred while retrieving ids", e);
            throw new RuntimeException("Failed to retrieve Ids");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving ids", e);
            throw new RuntimeException("Unexpected Error");
        }
    }

    public Coin getLatestTokenPriceBySymbol(String symbol) {
        try {
            logger.info("Fetching latest token price record for symbol: {}", symbol);
            Coin coin = coinStorageManager.getLatestPriceBySymbol(symbol);
            if (coin == null) {
                throw new CoinNotFoundException(symbol);
            }
            return coin;
        } catch (SQLException e) {
            logger.error("Database error occurred while fetching latest price for symbol: {}", symbol, e);
            throw new RuntimeException("Database Error");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching latest price for symbol: {}", symbol, e);
            throw new RuntimeException("Unexpected Error");
        }
    }

    public Coin getHighestTokenPriceBySymbol(String symbol) {
        try {
            logger.info("---------> Fetching highest token price record for symbol: {}", symbol);
            Coin coin = coinStorageManager.getHighestPriceBySymbol(symbol);
            if (coin == null) {
                throw new CoinNotFoundException(symbol);
            }
            return coin;
        } catch (SQLException e) {
            logger.error("Database error occurred while fetching highest price for symbol: {}", symbol, e);
            throw new RuntimeException("Database Error");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching highest price for symbol: {}", symbol, e);
            throw new RuntimeException("Unexpected Error");
        }
    }
}

