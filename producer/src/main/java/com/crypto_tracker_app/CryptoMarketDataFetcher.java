package com.crypto_tracker_app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CryptoMarketDataFetcher {
    private static final String KAFKA_BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private static final String TOPIC = System.getenv("KAFKA_TOPIC");
    private static final String COINGECKO_API_KEY = System.getenv("COINGECKO_API_KEY");

    private final KafkaProducer<String, String> producer;

    public CryptoMarketDataFetcher() {
        // Kafka config
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }


    public void getCryptoMetadata(int totalPages) {
        System.out.println("-------------- Start running getCryptoMetadata function ---------------");
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
                System.err.printf("Error processing page %d: %s%n", page, e.getMessage());
                e.printStackTrace();
            }
        }

        long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("Total fetch time: " + durationSeconds + " seconds");
    }

    private String fetchPageFromCoinGecko(int page, HttpClient httpClient) throws IOException, InterruptedException {
        String url = String.format("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=%d", page);
        System.out.println("Request URL: " + url);

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
        JSONArray coins = new JSONArray(pageData);
        return mapper.readValue(coins.toString(), new TypeReference<List<Coin>>() {});
    }

    private void sendCoinsToKafka(List<Coin> coins, ObjectMapper mapper) throws IOException {
        for (Coin coin : coins) {
            String coinJson = mapper.writeValueAsString(coin);
            System.out.printf("Sending %s: %s%n", coin.getId(), coinJson);
            producer.send(new ProducerRecord<>(TOPIC, coin.getId(), coinJson));
        }

        producer.flush();
    }
        


    public void close() {
        producer.close();
    }
}
