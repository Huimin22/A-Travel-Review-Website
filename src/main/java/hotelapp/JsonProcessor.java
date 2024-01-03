package hotelapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class JsonProcessor {
    /**
     * Parses a JSON file containing hotel data and returns a list of Hotel objects.
     *
     * @param filePath The path to the JSON file to be parsed.
     * @return A List of Hotel objects representing the data from the JSON file.
     */
    public static List<Hotel> parseHotelFile(String filePath) {
        List<Hotel> hotels = new ArrayList<>();
        Gson gson = new Gson();
        try (FileReader fr = new FileReader(filePath)) {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray jsonArr = jo.getAsJsonArray("sr");
            Type hotelType = new TypeToken<ArrayList<Hotel>>(){}.getType();
            hotels = gson.fromJson(jsonArr, hotelType);
        } catch (IOException e) {
            System.out.println("Could not read the file: " + e);
        }

        return hotels;
    }

    /**
     * Parses a single JSON file containing hotel review data and returns a list of Review objects.
     *
     * @param filePath The path to the JSON file to be parsed.
     * @return A List of Review objects representing the review data from the JSON file.
     */
    public static List<Review> parseSingleReviewFile(String filePath) {
        List<Review> reviews = new ArrayList<>();
        Gson gson = new Gson();
        try (FileReader fr = new FileReader(filePath)) {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray reviewArray = jo.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection").getAsJsonArray("review");
            Type reviewType = new TypeToken<ArrayList<Review>>(){}.getType();
            reviews = gson.fromJson(reviewArray, reviewType);
        } catch (IOException e) {
            System.out.println("Could not read the file: " + e);
        }
        return reviews;
    }

}