async function fetchTopCoins() {
  const limit = document.getElementById("limit").value;
  const coinsDiv = document.getElementById("coins");
  coinsDiv.innerHTML = "";

  try {
    const response = await fetch(`http://localhost:8080/tokens/top_coins/${limit}`);
    if (!response.ok) throw new Error("Network response was not ok");

    const coins = await response.json();

    // Save to localStorage so coins can be restored if the page reloads
    localStorage.setItem("lastLimit", limit);
    localStorage.setItem("lastCoins", JSON.stringify(coins)); // localStorage stores only strings

    renderCoins(coins);
  } catch (error) {
    coinsDiv.innerHTML = `<p style="color:red;">Error fetching data: ${error.message}</p>`;
    console.error("Fetch error:", error);
  }
}

function renderCoins(coins) {
  const coinsDiv = document.getElementById("coins");
  coinsDiv.innerHTML = "";

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

    // Add click handler to redirect to coin detail page
    card.addEventListener("click", () => {
      window.location.href = `coin.html?id=${coin.id}`;
    });

    coinsDiv.appendChild(card);
  });
}

// Restore last viewed list on page load
document.addEventListener("DOMContentLoaded", () => {
  const lastLimit = localStorage.getItem("lastLimit");
  const lastCoins = localStorage.getItem("lastCoins");

  if (lastLimit && lastCoins) {
    document.getElementById("limit").value = lastLimit;
    const coins = JSON.parse(lastCoins);
    renderCoins(coins);
  }
});
