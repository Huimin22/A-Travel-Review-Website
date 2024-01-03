package server;

import com.google.gson.JsonObject;
import hotelapp.Hotel;

public class WeatherFetch {

    /**
     * Retrieves weather data for a given hotel and formats it into a JSON string.
     *
     * @param hotel The Hotel object for which weather data is to be retrieved.
     * @return A JSON-formatted string containing weather data for the hotel.
     */
    public String getWeatherData(Hotel hotel) {
        String lat = String.valueOf(hotel.getLatitude());
        String lng = String.valueOf(hotel.getLongitude());
        JsonObject fetchRes = HttpFetcher.fetch("api.open-meteo.com", "/v1/forecast?latitude="+ lat + "&longitude=" + lng + "&current_weather=true");
        JsonObject jsonRes = new JsonObject();
        if (fetchRes != null) {
            JsonObject currentWeather = fetchRes.getAsJsonObject("current_weather");
            String windSpeed = currentWeather.get("windspeed").getAsString();
            String temperature = currentWeather.get("temperature").getAsString();
            jsonRes.addProperty("hotelId", hotel.getHotelId());
            jsonRes.addProperty("name", hotel.getName());
            jsonRes.addProperty("windspeed", windSpeed);
            jsonRes.addProperty("temperature", temperature);
        }
        return jsonRes.toString();
    }

}
