CREATE TABLE IF NOT EXISTS coins (
                id TEXT,
                symbol TEXT,
                name TEXT,
                curr_timestamp TEXT,
                current_price DOUBLE PRECISION,
                market_cap BIGINT,
                market_cap_rank INT,
                total_volume BIGINT,
                high_24h DOUBLE PRECISION,
                low_24h DOUBLE PRECISION,
                ath DOUBLE PRECISION,
                ath_date TEXT,
                atl DOUBLE PRECISION,
                atl_date TEXT,
                last_updated TEXT,
                PRIMARY KEY (id, curr_timestamp)
            );