package com.crypto_tracker_app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CryptoMarketDataFetcher {
    private static final String KAFKA_BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private static final String TOPIC = System.getenv("KAFKA_TOPIC");
    private static final String COINGECKO_API_KEY = System.getenv("COINGECKO_API_KEY");
    private static final String URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=%d";
    private static final Logger logger = Logger.getLogger(CryptoMarketDataFetcher.class.getName());

    private final KafkaProducer<String, String> producer;

    public CryptoMarketDataFetcher() {
        // Kafka config
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }


    public void getCryptoCoinsInfo(int totalPages) {
        logger.info("-------------- Start running getCryptoCoinsInfo function ---------------");
        long startTime = System.currentTimeMillis();
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (int page = 1; page < totalPages; page++) {
            try {
                String fetchedPage = fetchPageFromCoinGecko(page, httpClient);
                List<Coin> coins = convertToCoinList(fetchedPage, mapper);
                sendCoinsToKafka(coins, mapper);
            } catch (IOException | InterruptedException e) {
                logger.log(Level.SEVERE, "Error processing page "+ page, e.getMessage());
            }
        }

        long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
        logger.info(String.format("Total fetch time: %,d seconds", durationSeconds));
    }

    private String fetchPageFromCoinGecko(int page, HttpClient httpClient) throws IOException, InterruptedException {
        String url = String.format(URL, page);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("x-cg-demo-api-key", COINGECKO_API_KEY)
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status: " + response.statusCode() + " Body: " + response.body());
        }

        return response.body();
    }

    private List<Coin> convertToCoinList(String pageData, ObjectMapper mapper) throws IOException {
        List<Coin> coinsList = mapper.readValue(pageData, new TypeReference<List<Coin>>() {});
        logger.log(Level.INFO, ">>>> This is Coins List from a single page: ", coinsList);
        return coinsList;
    }

    private void sendCoinsToKafka(List<Coin> coins, ObjectMapper mapper) throws IOException {
        for (Coin coin : coins) {
            String coinJson = mapper.writeValueAsString(coin);
            logger.info(String.format("Sending %s: %s%n", coin.getId(), coinJson));
            producer.send(new ProducerRecord<>(TOPIC, coin.getId(), coinJson));
        }

        producer.flush();
    }
        


    public void close() {
        producer.close();
    }
}
