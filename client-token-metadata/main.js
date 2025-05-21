async function fetchTopCoins() {
    const limit = document.getElementById("limit").value;
    const coinsDiv = document.getElementById("coins");
    coinsDiv.innerHTML = ""; // clear previous results

    try {
        const response = await fetch(`http://localhost:8080/tokens/top_coins/${limit}`);
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        const coins = await response.json();

        coins.forEach(coin => {
            const card = document.createElement("div");
            card.className = "coin-card";
            card.innerHTML = `
                <h3><img src="${coin.image}" alt="${coin.name}"> ${coin.name} (${coin.symbol.toUpperCase()})</h3>
                <p><strong>Price:</strong> $${coin.current_price.toLocaleString()}</p>
                <p><strong>Market Cap:</strong> $${coin.market_cap.toLocaleString()}</p>
                <p><strong>Rank:</strong> ${coin.market_cap_rank}</p>
                <p><strong>High 24h:</strong> $${coin.high_24h}</p>
                <p><strong>Low 24h:</strong> $${coin.low_24h}</p>
            `;
            coinsDiv.appendChild(card);
        });
    } catch (error) {
        coinsDiv.innerHTML = `<p style="color:red;">Error fetching data: ${error.message}</p>`;
        console.error("Fetch error:", error);
    }
}