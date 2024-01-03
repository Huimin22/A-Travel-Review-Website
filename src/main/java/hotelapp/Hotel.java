package hotelapp;

import com.google.gson.annotations.SerializedName;

public class Hotel {
    @SerializedName(value = "f")
    private final String name;
    @SerializedName(value = "id")
    private final String hotelId;
    @SerializedName(value = "ll")
    private Location location;
    @SerializedName(value = "ad")
    private final String address;
    @SerializedName(value = "ci")
    private final String city;
    @SerializedName(value = "pr")
    private final String state;
    @SerializedName(value = "c")
    private final String country;

    public static class Location {
        private double lat;
        private double lng;

        public Location(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        /**
         * Get the latitude of the hotel's location.
         * @return The latitude.
         */
        public double getLat() {
            return lat;
        }

        /**
         * Get the longitude of the hotel's location.
         * @return The longitude.
         */
        public double getLng() {
            return lng;
        }
    }

    /**
     * Constructor to create a Hotel object.
     * @param name The name of the hotel.
     * @param hotelId The unique identifier of the hotel.
     * @param address The street address of the hotel.
     * @param city The city where the hotel is located.
     * @param state The state or region where the hotel is located.
     * @param country The country where the hotel is located.
     */
    public Hotel(String name, String hotelId, String address, String city, String state, String country) {
        this.name = name;
        this.hotelId = hotelId;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public Hotel(String name, String hotelId, String address, String city, String state, String country, Location location) {
        this.name = name;
        this.hotelId = hotelId;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.location = location;

    }

    /**
     * Get the name of the hotel.
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the unique identifier of the hotel.
     * @return The hotel ID.
     */
    public String getHotelId() {
        return this.hotelId;
    }

    /**
     * Get the latitude of the hotel's location.
     * @return The latitude.
     */
    public double getLatitude() {
        return this.location.getLat();
    }

    /**
     * Get the longitude of the hotel's location.
     * @return The longitude.
     */
    public double getLongitude() {
        return this.location.getLng();
    }

    /**
     * Get the street address of the hotel.
     * @return The address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Get the city and state where the hotel is located.
     * @return The city and state.
     */
    public String getCityAndState() {
        return this.city + ", " + this.state;
    }

    /**
     * Retrieves the city associated with this object.
     *
     * @return The name of the city.
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Retrieves the state associated with this object.
     *
     * @return The name of the state.
     */
    public String getState() {
        return this.state;
    }

    /**
     * Retrieves the country associated with this object.
     *
     * @return The name of the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Get the full address of the hotel, including street address, city, state, and country.
     * @return The full address.
     */
    public String getFullAddress() {
        return this.address + ", " + this.city + ", " + this.state + ", " + this.country;
    }

}
