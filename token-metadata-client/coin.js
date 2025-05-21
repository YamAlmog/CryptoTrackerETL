// coin.js
// Helper to get query string params
function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

async function fetchCoinChart() {
    const coinId = getQueryParam("id");
    const container = document.getElementById("coin-details");
    
    if (!coinId) {
        container.innerHTML = "<p style='color:red;'>No coin ID provided in URL.</p>";
        return;
    }
    
    try {
        // Show loading message
        container.innerHTML = "<p>Loading chart data...</p>";
        
        // Fetch market chart data from CoinGecko API
        const response = await fetch(`https://api.coingecko.com/api/v3/coins/${coinId}/market_chart?vs_currency=usd&days=14`);
        
        if (!response.ok) {
            throw new Error("Failed to fetch chart data");
        }
        
        const chartData = await response.json();
        
        // Also fetch basic coin info to display name and image
        const coinInfoResponse = await fetch(`https://api.coingecko.com/api/v3/coins/${coinId}?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false`);
        const coinInfo = await coinInfoResponse.json();
        
        // Process price data for the chart
        const prices = chartData.prices.map(item => {
            return {
                date: new Date(item[0]),
                price: item[1]
            };
        });
        
        // Create HTML structure
        container.innerHTML = `
            <h1><img src="${coinInfo.image.small}" alt="${coinInfo.name}"> ${coinInfo.name} (${coinInfo.symbol.toUpperCase()}) Price Chart (14 Days)</h1>
            <div class="chart-container">
                <canvas id="priceChart"></canvas>
            </div>
            <div class="price-info">
                <p><strong>Current Price:</strong> $${prices[prices.length-1].price.toLocaleString(undefined, {maximumFractionDigits: 2})}</p>
                <p><strong>14d High:</strong> $${Math.max(...prices.map(p => p.price)).toLocaleString(undefined, {maximumFractionDigits: 2})}</p>
                <p><strong>14d Low:</strong> $${Math.min(...prices.map(p => p.price)).toLocaleString(undefined, {maximumFractionDigits: 2})}</p>
                <p><a href="index.html">← Back to coin list</a></p>
            </div>
        `;
        
        // Create the chart using Chart.js
        createChart(prices);
        
    } catch (error) {
        container.innerHTML = `<p style='color:red;'>Error loading chart: ${error.message}</p>`;
        console.error(error);
    }
}

function createChart(prices) {
    const ctx = document.getElementById('priceChart').getContext('2d');
    
    // Format dates for labels
    const labels = prices.map(dataPoint => {
        const date = dataPoint.date;
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    });
    
    // Price data
    const priceData = prices.map(dataPoint => dataPoint.price);
    
    // Create gradient for chart background
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, 'rgba(54, 162, 235, 0.2)');
    gradient.addColorStop(1, 'rgba(54, 162, 235, 0)');
    
    // Create the chart
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Price (USD)',
                data: priceData,
                borderColor: 'rgb(54, 162, 235)',
                backgroundColor: gradient,
                borderWidth: 2,
                pointRadius: 0,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: 'rgb(54, 162, 235)',
                tension: 0.1,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index',
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `$${context.parsed.y.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
                        }
                    }
                },
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    }
                },
                y: {
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toLocaleString();
                        }
                    }
                }
            }
        }
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', fetchCoinChart);