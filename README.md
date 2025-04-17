# Crypto Market Tracker ETL

This project collects real-time cryptocurrency price data from the CoinGecko API, sends it to Kafka using a producer app, and stores it in a PostgreSQL database using a consumer app.

## How to Run the Project with Docker

On your host machine:

cd CryptoTrackerETL

mvn clean install -DskipTests

docker-compose up --build