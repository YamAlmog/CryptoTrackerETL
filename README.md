# Crypto Market Tracker ETL 🚀

A real-time ETL pipeline for cryptocurrency market data, built with Kafka, PostgreSQL, and Spring Boot.


### Project Highlights:

🔄 Collects live data from the CoinGecko API.

📨 Streams data through Apache Kafka (Producer app).

🛢️ Stores token market data into PostgreSQL (Consumer app).

🌐 Exposes a REST API with Spring Boot to access token metadata.

📦 Fully containerized using Docker and Docker Compose for easy deployment.


## System Flow

```mermaid
flowchart LR
    A[🌎 CoinGecko API] --> B[⚡ Kafka Producer App]
    B --> C[🛠️ Apache Kafka]
    C --> D[⚙️ Kafka Consumer App]
    D --> E[🛢️ PostgreSQL Database]
    E --> F[🌐 Spring Boot API Service]
```


## How to Run the Project with Docker

```bash
cd CryptoTrackerETL
mvn clean install -DskipTests
docker-compose up --build
```
