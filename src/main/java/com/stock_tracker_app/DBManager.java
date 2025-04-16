package com.stock_tracker_app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {

    private static final String URL = System.getenv("DB_URL");    
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public DBManager() {}


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
            pstmt.setString(4, coin.getCurrTimestamp());
            pstmt.setDouble(5, coin.getCurrentPrice());
            pstmt.setLong(6, coin.getMarketCap());
            pstmt.setInt(7, coin.getMarketCapRank());
            pstmt.setLong(8, coin.getTotalVolume());
            pstmt.setDouble(9, coin.getHigh24h());
            pstmt.setDouble(10, coin.getLow24h());
            pstmt.setDouble(11, coin.getAth());
            pstmt.setString(12, coin.getAthDate().toString());
            pstmt.setDouble(13, coin.getAtl());
            pstmt.setString(14, coin.getAtlDate().toString());
            pstmt.setString(15, coin.getLastUpdated().toString());

            pstmt.executeUpdate();
            System.out.println("Coin inserted: " + coin.getId() + " @ " + coin.getCurrTimestamp());

        } catch (SQLException e) {
            System.err.println("Error inserting coin: " + coin.getId());
            e.printStackTrace();
        }
    }
}