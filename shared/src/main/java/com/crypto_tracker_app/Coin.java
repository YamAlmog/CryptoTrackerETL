package com.crypto_tracker_app;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Coin {

    private String id;
    private String symbol;
    private String name;
    private ZonedDateTime currTimestamp;

    @JsonProperty("current_price")
    private Double currentPrice;

    @JsonProperty("market_cap")
    private Long marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("total_volume")
    private Long totalVolume;

    @JsonProperty("high_24h")
    private Double high24h;

    @JsonProperty("low_24h")
    private Double low24h;

    private Double ath;

    @JsonProperty("ath_date")
    private ZonedDateTime athDate;  // allow nulls here

    private Double atl;

    @JsonProperty("atl_date")
    private ZonedDateTime atlDate;  // allow nulls here

    @JsonProperty("last_updated")
    private ZonedDateTime lastUpdated;

    // Getters and Setters...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getCurrTimestamp() {
        return currTimestamp;
    }

    public void setCurrTimestamp(ZonedDateTime currTimestamp) {
        this.currTimestamp = currTimestamp;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    public Integer getMarketCapRank() {
        return marketCapRank;
    }

    public void setMarketCapRank(Integer marketCapRank) {
        this.marketCapRank = marketCapRank;
    }

    public Long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Long totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Double getHigh24h() {
        return high24h;
    }

    public void setHigh24h(Double high24h) {
        this.high24h = high24h;
    }

    public Double getLow24h() {
        return low24h;
    }

    public void setLow24h(Double low24h) {
        this.low24h = low24h;
    }

    public Double getAth() {
        return ath;
    }

    public void setAth(Double ath) {
        this.ath = ath;
    }

    public ZonedDateTime getAthDate() {
        return athDate;
    }

    public void setAthDate(ZonedDateTime athDate) {
        this.athDate = athDate;
    }

    public Double getAtl() {
        return atl;
    }

    public void setAtl(Double atl) {
        this.atl = atl;
    }

    public ZonedDateTime getAtlDate() {
        return atlDate;
    }

    public void setAtlDate(ZonedDateTime atlDate) {
        this.atlDate = atlDate;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", currTimestamp='" + currTimestamp + '\'' +
                ", currentPrice=" + currentPrice +
                ", marketCap=" + marketCap +
                ", marketCapRank=" + marketCapRank +
                ", totalVolume=" + totalVolume +
                ", high24h=" + high24h +
                ", low24h=" + low24h +
                ", ath=" + ath +
                ", athDate=" + athDate +
                ", atl=" + atl +
                ", atlDate=" + atlDate +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}