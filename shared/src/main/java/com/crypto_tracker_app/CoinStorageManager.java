package com.crypto_tracker_app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoinStorageManager {

    private static final String URL = System.getenv("DB_URL");    
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static final Logger logger = Logger.getLogger(CoinStorageManager.class.getName());

    public CoinStorageManager() {}

    public void insertCoinToDB(Coin coin) {
        String sql = """
            INSERT INTO coins (
                id, symbol, name, current_price,
                market_cap, market_cap_rank, total_volume, high_24h, low_24h,
                ath, ath_date, atl, atl_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id, curr_timestamp) DO NOTHING;
        """;
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, coin.getId());
            pstmt.setString(2, coin.getSymbol());
            pstmt.setString(3, coin.getName());
            pstmt.setObject(4, coin.getCurrentPrice(), java.sql.Types.DOUBLE);
            pstmt.setObject(5, coin.getMarketCap(), java.sql.Types.BIGINT);
            pstmt.setObject(6, coin.getMarketCapRank(), java.sql.Types.INTEGER);
            pstmt.setObject(7, coin.getTotalVolume(), java.sql.Types.BIGINT);
            pstmt.setObject(8, coin.getHigh24h(), java.sql.Types.DOUBLE);
            pstmt.setObject(9, coin.getLow24h(), java.sql.Types.DOUBLE);
            pstmt.setObject(10, coin.getAth(), java.sql.Types.DOUBLE);
            pstmt.setString(11, coin.getAthDate() != null ? coin.getAthDate().toString() : null);
            pstmt.setObject(12, coin.getAtl(), java.sql.Types.DOUBLE);
            pstmt.setString(13, coin.getAtlDate() != null ? coin.getAtlDate().toString() : null);
    
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Coin inserted: ", coin.getId());
    
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error inserting coin: "+ coin.getId(), e);
        }
    }

    public List<String> getAllTokenSymbols() throws SQLException {
        logger.info("---------> Inside CoinStorageManager getAllTokenSymbols function");
        String sql = "SELECT DISTINCT symbol FROM coins";
        List<String> symbols = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            if(!rs.next()){
                return null;
            }
            while (rs.next()) {
                symbols.add(rs.getString("symbol"));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving token symbols", e);
            throw e;  // Re-throw the exception after logging it
        }
        return symbols;
    }


    public List<String> getAllTokenIds() throws SQLException {
        logger.info("---------> Inside CoinStorageManager getAllTokenIds function");
        String sql = "SELECT DISTINCT id FROM coins";
        List<String> idsList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            if(!rs.next()){
                return null;
            }
            while (rs.next()) {
                idsList.add(rs.getString("id"));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving token Ids", e);
            throw e;  // Re-throw the exception after logging it
        }
        return idsList;
    }

    public Coin getLatestPriceBySymbol(String symbol) throws SQLException {
        String sql = """
            SELECT id, symbol, name,curr_timestamp, current_price, market_cap, low_24h, high_24h FROM coins
            WHERE symbol = ?
            ORDER BY curr_timestamp DESC
            LIMIT 1
        """;
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, symbol);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return buildCoinFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving latest record for symbol: "+ symbol, e);
            throw e;  
        }
    }
    
    public Coin getHighestPriceBySymbol(String symbol) throws SQLException {
        String sql = """
            SELECT id, symbol, name, curr_timestamp, current_price, market_cap, low_24h, high_24h FROM coins
            WHERE symbol = ?
            ORDER BY current_price DESC
            LIMIT 1
        """;
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, symbol);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return buildCoinFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving record with highest price for symbol: "+ symbol, e);
            throw e;  
        }
    }

    public Coin buildCoinFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String coinSymbol = rs.getString("symbol");
        String name =  rs.getString("name");
        ZonedDateTime currTimestamp = rs.getTimestamp("curr_timestamp").toInstant().atZone(java.time.ZoneId.systemDefault()); // Map curr_timestamp to ZonedDateTime
        logger.log(Level.INFO, "currTimestamp: {0}", currTimestamp);
        Double currPrice = rs.getDouble("current_price");
        Long marketCap = rs.getLong("market_cap");
        double low24h =  rs.getDouble("high_24h");
        double high24h  = rs.getDouble("low_24h");

        Coin coin = new Coin(id, coinSymbol, name, currTimestamp, currPrice, marketCap, low24h, high24h);
        return coin;
    }
}