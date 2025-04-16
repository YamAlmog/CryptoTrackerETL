package com.stock_tracker_app;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CryptoPriceConsumer {

    private static final String BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    private static final String TOPIC = System.getenv("KAFKA_TOPIC");
    private static final String GROUP_ID = "crypto-price-consumer-group";

    DBManager dbManager = new DBManager();
    private final ObjectMapper mapper;

    public CryptoPriceConsumer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public void startConsuming() {
        System.out.println("------------> Inside startConsuming()");
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            System.out.println("Started consuming from topic: " + TOPIC);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String jsonValue = record.value();
                    try {
                        Coin coin = mapper.readValue(jsonValue, Coin.class);
                        System.out.printf("----------------->>> Consumer Received %s: %s%n", key, coin);
                        dbManager.insertCoinToDB(coin);
                        // You can add more processing here (save to DB, alerting, etc.)
                    } catch (Exception e) {
                        System.err.println("Failed to parse message: " + jsonValue);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}