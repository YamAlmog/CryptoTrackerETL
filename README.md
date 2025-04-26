# Crypto Market Tracker ETL

This project implements a complete ETL pipeline for real-time cryptocurrency market data. 
It collects live data from the CoinGecko API, streams it through Apache Kafka, and stores it in a PostgreSQL database with historical tracking and automatic timestamping. 
In addition, a Spring Boot web service exposes a REST API for accessing token metadata directly from the database. 
The entire system is containerized with Docker for easy deployment.

## How to Run the Project with Docker

On your host machine:

cd CryptoTrackerETL

mvn clean install -DskipTests

docker-compose up --build