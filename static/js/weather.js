async function showWeatherData(hotelId) {
    try {
        let response = await fetch('/getWeather?hotelId=' + hotelId, { method: 'get' });
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        let jsonData = await response.json();

        updateWeatherDisplay(jsonData);
    } catch (error) {
        console.error('Error fetching weather data:', error);
    }
}

function updateWeatherDisplay(weatherData) {
    const temperatureLabel = "Temperature";
    const windSpeedLabel = "Wind Speed";
    document.getElementById("tlabel").innerHTML = temperatureLabel;
    document.getElementById("wlabel").innerHTML = windSpeedLabel;
    document.getElementById("temperature").innerHTML = weatherData.temperature;
    document.getElementById("windspeed").innerHTML = weatherData.windspeed;
}


