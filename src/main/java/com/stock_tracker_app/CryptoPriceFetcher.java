package com.stock_tracker_app;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.util.*;

public class CryptoPriceFetcher {
    private static final String KAFKA_BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private static final String TOPIC = "crypto-prices";

    private final KafkaProducer<String, String> producer;

    public CryptoPriceFetcher() {
        // Kafka config
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    public void getCryptoPrices(List<String> coinIds) throws IOException, InterruptedException {
        if (coinIds.isEmpty()) {
            System.out.println("No coins provided.");
            return;
        }

        String idsParam = String.join(",", coinIds);
        System.out.println("--------------------------- idsParam -------------------------"+idsParam);
        String url = String.format("https://api.coingecko.com/api/v3/simple/price?ids=%s&vs_currencies=usd", idsParam);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Failed to fetch crypto prices. Status: " + response.statusCode());
            return;
        }

        JSONObject prices = new JSONObject(response.body());
        String timestamp = Instant.now().toString();

        for (String coin : coinIds) {
            if (!prices.has(coin)) {
                System.err.println("No data found for: " + coin);
                continue;
            }

            double price = prices.getJSONObject(coin).getDouble("usd");

            JSONObject messageJson = new JSONObject();
            messageJson.put("symbol", coin);
            messageJson.put("price", price);
            messageJson.put("timestamp", timestamp);

            System.out.printf("%s: $%.2f at %s%n", coin, price, timestamp);

            producer.send(new ProducerRecord<>(TOPIC, coin, messageJson.toString()));
        }

        producer.flush();
    }

    public void close() {
        producer.close();
    }
}
