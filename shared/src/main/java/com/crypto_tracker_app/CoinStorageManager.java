package com.crypto_tracker_app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoinStorageManager {

    private static final String URL = System.getenv("DB_URL");    
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public CoinStorageManager() {}


    public void insertCoinToDB(Coin coin) {
        String sql = """
            INSERT INTO coins (
                id, symbol, name, curr_timestamp, current_price,
                market_cap, market_cap_rank, total_volume, high_24h, low_24h,
                ath, ath_date, atl, atl_date, last_updated
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id, curr_timestamp) DO NOTHING;
        """;
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, coin.getId());
            pstmt.setString(2, coin.getSymbol());
            pstmt.setString(3, coin.getName());
            pstmt.setString(4, coin.getCurrTimestamp() != null ? coin.getCurrTimestamp().toString() : null);
            pstmt.setObject(5, coin.getCurrentPrice(), java.sql.Types.DOUBLE);
            pstmt.setObject(6, coin.getMarketCap(), java.sql.Types.BIGINT);
            pstmt.setObject(7, coin.getMarketCapRank(), java.sql.Types.INTEGER);
            pstmt.setObject(8, coin.getTotalVolume(), java.sql.Types.BIGINT);
            pstmt.setObject(9, coin.getHigh24h(), java.sql.Types.DOUBLE);
            pstmt.setObject(10, coin.getLow24h(), java.sql.Types.DOUBLE);
            pstmt.setObject(11, coin.getAth(), java.sql.Types.DOUBLE);
            pstmt.setString(12, coin.getAthDate() != null ? coin.getAthDate().toString() : null);
            pstmt.setObject(13, coin.getAtl(), java.sql.Types.DOUBLE);
            pstmt.setString(14, coin.getAtlDate() != null ? coin.getAtlDate().toString() : null);
            pstmt.setString(15, coin.getLastUpdated() != null ? coin.getLastUpdated().toString() : null);
    
            pstmt.executeUpdate();
            System.out.println("Coin inserted: " + coin.getId() + " @ " + coin.getCurrTimestamp());
    
        } catch (SQLException e) {
            System.err.println("Error inserting coin: " + coin.getId());
            e.printStackTrace();
        }
    }

    public Coin getLatestPriceBySymbol(String symbol) throws SQLException {
        String sql = """
            SELECT id, symbol, name, current_price, market_cap, low_24h, high_24h FROM coins
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
        }
    }
    
    public Coin getHighestPriceBySymbol(String symbol) throws SQLException {
        String sql = """
            SELECT id, symbol, name, current_price, market_cap, low_24h, high_24h FROM coins
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
        }
    }

    public Coin buildCoinFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String coin_symbol = rs.getString("symbol");
        String name =  rs.getString("name");
        Double curr_price = rs.getDouble("current_price");
        Long market_cap = rs.getLong("market_cap");
        double low_24h =  rs.getDouble("high_24h");
        double high_24h  = rs.getDouble("low_24h");

        Coin coin = new Coin(id, coin_symbol, name, curr_price, market_cap, low_24h, high_24h);
        return coin;
    }
}