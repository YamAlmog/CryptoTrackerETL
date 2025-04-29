package com.crypto_tracker_app;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CryptoMarketDataConsumer {

    private static final String BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private static final String TOPIC = System.getenv("KAFKA_TOPIC");
    private static final String GROUP_ID = "crypto-price-consumer-group";
    private static final long POLL_TIMEOUT_MS = 1000;
    private static final Logger logger = Logger.getLogger(CryptoMarketDataConsumer.class.getName());

    private KafkaConsumer<String, String> consumer;
    
    private final CoinStorageManager coinStorageManager;
    private final ObjectMapper mapper;

    public CryptoMarketDataConsumer() {
        coinStorageManager = new CoinStorageManager();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public void startConsuming() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(props);

        try{
            consumer.subscribe(Collections.singletonList(TOPIC));
            logger.log(Level.INFO, "Started consuming from topic: ", TOPIC);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT_MS));
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String jsonValue = record.value();
                    try {
                        Coin coin = mapper.readValue(jsonValue, Coin.class);
                        logger.info(String.format("----------------->>> Consumer Received %s: %s%n", key, coin));
                        coinStorageManager.insertCoinToDB(coin);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to parse message: " + jsonValue, e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();  // Ensure that we close the consumer when done
        }
    }

    public void close() {
        if (consumer != null) {
            logger.info("Closing Kafka consumer...");
            consumer.close();  // Graceful shutdown
        }
    }
}