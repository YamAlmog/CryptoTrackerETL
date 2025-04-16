package com.stock_tracker_app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CryptoPriceFetcher {
    private static final String KAFKA_BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private static final String TOPIC = System.getenv("KAFKA_TOPIC");
    private static final String COINGECKO_API_KEY = System.getenv("COINGECKO_API_KEY");

    private final KafkaProducer<String, String> producer;

    public CryptoPriceFetcher() {
        // Kafka config
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    public void getCryptoPrices(int totalPages) throws IOException, InterruptedException {
        System.out.println("-------------- Start running getCryptoPrices function ---------------");
        long startTime = System.currentTimeMillis(); // start timing

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String timestamp = Instant.now().toString();

        for(int page = 1; page < totalPages; page++){    
            
            String url = String.format("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=%d", page);

            System.out.println("Request URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-cg-demo-api-key", COINGECKO_API_KEY)
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Failed to fetch crypto prices. Status: " + response.statusCode());
                System.err.println("Failed to fetch crypto prices. Body: " + response.body());
                return;
            }

            JSONArray coins = new JSONArray(response.body());

            // Convert JSONArray to List<Coin>
            List<Coin> coinList = mapper.readValue(coins.toString(), new TypeReference<List<Coin>>() {});

            // Loop through each Coin object and send it as JSON to Kafka
            for (Coin coin : coinList) {
                String coinId = coin.getId(); // use as Kafka key
                coin.setCurrTimestamp(timestamp); // set the curr timestamp for coin object
                String coinJson = mapper.writeValueAsString(coin); // full coin object as JSON string

                System.out.printf("Sending %s: %s%n", coinId, coinJson);
                producer.send(new ProducerRecord<>(TOPIC, coinId, coinJson));
            }

            producer.flush();

        }
        
        long endTime = System.currentTimeMillis(); // End timing
        long durationSeconds = (endTime - startTime) / 1000;
        System.out.println("Total fetch time: " + durationSeconds + " seconds");

    }

    public void close() {
        producer.close();
    }
}
