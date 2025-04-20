package com.crypto_tracker_app;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


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
            pstmt.setString(4, coin.getCurrTimestamp().toString());
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

    public Coin getLatestCoinBySymbol(String symbol) throws SQLException {
        String sql = """
            SELECT * FROM coins
            WHERE symbol = ?
            ORDER BY curr_timestamp DESC
            LIMIT 1
        """;
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, symbol);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                System.out.println("Looking for symbol: '" + symbol + "'");
                System.out.println("Executing query...");
                return buildCoinFromResultSet(rs);
            } else {
                System.out.println("No result found for symbol: '" + symbol + "'");
                return null;
            }
        }
    }
    
    public Coin getHighestCoinBySymbol(String symbol) throws SQLException {
        String sql = """
            SELECT * FROM coins
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
        Coin coin = new Coin();
        coin.setId(rs.getString("id"));
        coin.setSymbol(rs.getString("symbol"));
        coin.setName(rs.getString("name"));
        
        // Convert timestamp (UTC in DB) back to ZonedDateTime
        coin.setCurrTimestamp(rs.getObject("curr_timestamp", ZonedDateTime.class));

        coin.setCurrentPrice(rs.getDouble("current_price"));
        coin.setMarketCap(rs.getLong("market_cap"));
        coin.setMarketCapRank(rs.getInt("market_cap_rank"));
        coin.setTotalVolume(rs.getLong("total_volume"));
        coin.setHigh24h(rs.getDouble("high_24h"));
        coin.setLow24h(rs.getDouble("low_24h"));
        coin.setAth(rs.getDouble("ath"));
        coin.setAthDate(rs.getTimestamp("ath_date").toLocalDateTime().atZone(ZoneId.systemDefault()));
        coin.setAtl(rs.getDouble("atl"));
        coin.setAtlDate(rs.getTimestamp("atl_date").toLocalDateTime().atZone(ZoneId.systemDefault()));
        coin.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime().atZone(ZoneId.systemDefault()));
        return coin;
    }
}