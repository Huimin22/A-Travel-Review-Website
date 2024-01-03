package server;

import hotelapp.Hotel;
import hotelapp.Review;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseHandler {

    private static DatabaseHandler dbHandler = new DatabaseHandler("database.properties"); // singleton pattern
    private Properties config; // a "map" of properties
    private String uri = null; // uri to connect to mysql using jdbc
    private Random random = new Random(); // used in password  generation

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private DatabaseHandler(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    /**
     * Returns the instance of the database handler.
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return dbHandler;
    }

    // Load info from config file database.properties
    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);
        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newUser - username of new user
     * @param newPass - password of new user
     */
    public void registerUser(String newUser, String newPass) {
        // Generate salt
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32); // salt
        String passhash = getHash(newPass, usersalt); // hashed password
        System.out.println(usersalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newUser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Authenticates a user by verifying the provided username and password against the database.
     *
     * @param username The username to be authenticated.
     * @param password The password to be verified.
     * @return True if authentication is successful; false otherwise.
     */
    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        boolean flag = false;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String userSalt = getSalt(connection, username);
            String passHash = getHash(password, userSalt);

            statement.setString(1, username);
            statement.setString(2, passHash);
            ResultSet results = statement.executeQuery();
            flag = results.next();
            return flag;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return flag;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }

    /**
     * Retrieves the names of all tables in the database.
     *
     * @return A list containing the names of all tables in the database.
     */
    public List<String> getTablesName() {
        PreparedStatement statement;
        List<String> tables = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SHOW_TABLES);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                tables.add(results.getString("Tables_in_mydb"));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return tables;
    }

    /**
     * Creates the "users" table if it does not already exist in the database.
     */
    public void createUsersTable() {
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            List<String> allTableNames = getTablesName();
            if (!allTableNames.contains("users")) {
                // The "user" table does not exist, create it
                try (Statement statement = dbConnection.createStatement()) {
                    statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
                    System.out.println("Users table created successfully");
                } catch (SQLException createTableEx) {
                    System.out.println("Error creating user table: " + createTableEx);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex);
        }
    }

    /**
     * Creates the "hotels" table if it does not already exist in the database.
     */
    public void createHotelsTable() {
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            // Retrieve all table names in the database
            List<String> allTableNames = getTablesName();
            // Check if the "hotels" table exists, create it if not
            if (!allTableNames.contains("hotels")) {
                try (Statement statement = dbConnection.createStatement()) {
                    // Execute the SQL statement to create the "hotels" table
                    statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
                    System.out.println("Hotels table created successfully");
                } catch (SQLException createTableEx) {
                    // Handle errors during table creation and print details
                    System.out.println("Error creating hotels table: " + createTableEx);
                }
            }
        } catch (SQLException ex) {
            // Handle errors connecting to the database and print details
            System.out.println("Error connecting to the database: " + ex);
        }
    }

    /**
     * Creates the "reviews" table if it does not already exist in the database.
     */
    public void createReviewsTable() {
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            // Retrieve all table names in the database
            List<String> allTableNames = getTablesName();

            // Check if the "reviews" table exists, create it if not
            if (!allTableNames.contains("reviews")) {
                try (Statement statement = dbConnection.createStatement()) {
                    // Execute the SQL statement to create the "reviews" table
                    statement.executeUpdate(PreparedStatements.CREATE_REVIEWS_TABLE);
                    System.out.println("Reviews table created successfully");
                } catch (SQLException createTableEx) {
                    // Handle errors during table creation and print details
                    System.out.println("Error creating reviews table: " + createTableEx);
                }
            }
        } catch (SQLException ex) {
            // Handle errors connecting to the database and print details
            System.out.println("Error connecting to the database: " + ex);
        }
    }

    /**
     * Adds a list of hotels to the "hotels" table in the database.
     *
     * @param allHotels The list of Hotel objects to be added to the table.
     */
    public void addAllHotelsToTable(List<Hotel> allHotels) {
        if (allHotels == null || allHotels.isEmpty()) {
            System.out.println("The list of hotels is empty. No hotels to add.");
            return;
        }
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_ALL_HOTELS_TO_TABLE)) {
                for (Hotel hotel : allHotels) {
                    statement.setString(1, hotel.getHotelId());
                    statement.setString(2, hotel.getName());
                    statement.setDouble(3, hotel.getLatitude());
                    statement.setDouble(4, hotel.getLongitude());
                    statement.setString(5, hotel.getAddress());
                    statement.setString(6, hotel.getCity());
                    statement.setString(7, hotel.getState());
                    statement.setString(8, hotel.getCountry());
                    statement.executeUpdate();
                }
                System.out.println("Hotels added to the table successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding hotels to the table: " + e);
        }
    }

    /**
     * Adds a list of reviews to the "reviews" table in the database.
     *
     * @param allReviews The list of Review objects to be added to the table.
     */
    public void addAllReviewsToTable(List<Review> allReviews) {
        if (allReviews == null || allReviews.isEmpty()) {
            System.out.println("The list of reviews is empty. No reviews to add.");
            return;
        }
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_ALL_REVIEWS_TO_TABLE)) {
                for (Review review : allReviews) {
                    statement.setString(1, review.getReviewId());
                    statement.setString(2, Integer.toString(review.getHotelId()));
                    statement.setDouble(3, review.getRatingOverall());
                    statement.setString(4, review.getTitle());
                    statement.setString(5, review.getReviewText());
                    statement.setString(6, review.getUserNickname());
                    statement.setString(7, review.getDatePosted());
                    statement.executeUpdate();
                }
                System.out.println("reviews added to the table successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding reviews to the table: " + e);
        }
    }

    /**
     * Retrieves the usernames of all users from the "users" table.
     *
     * @return A list containing the usernames of all users.
     */
    public List<String> getAllUsers() {
        List<String> allUsers = new ArrayList<>();
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = dbConnection.prepareStatement(PreparedStatements.GET_ALL_USERS)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    allUsers.add(username);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        return allUsers;
    }

    /**
     * Retrieves a list of all hotels from the "hotels" table in the database.
     *
     * @return List of Hotel objects representing all hotels in the database.
     */
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_ALL_HOTELS)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String hotelID = resultSet.getString("hotelID");
                    String name = resultSet.getString("name");
                    double latitude = resultSet.getDouble("latitude");
                    double longitude = resultSet.getDouble("longitude");
                    String street = resultSet.getString("street");
                    String city = resultSet.getString("city");
                    String state = resultSet.getString("state");
                    String country = resultSet.getString("country");
                    Hotel.Location location = new Hotel.Location(latitude, longitude);
                    Hotel hotel = new Hotel(name, hotelID, street, city, state, country, location);
                    hotels.add(hotel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotels;
    }

    /**
     * Retrieves a specific hotel from the "hotels" table based on the provided hotelId.
     *
     * @param hotelId The unique identifier of the hotel to retrieve.
     * @return Hotel object representing the specified hotel or null if not found.
     */
    public Hotel getHotelById(String hotelId) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_HOTEL_BY_ID)) {
                statement.setString(1, hotelId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    double latitude = resultSet.getDouble("latitude");
                    double longitude = resultSet.getDouble("longitude");
                    String street = resultSet.getString("street");
                    String city = resultSet.getString("city");
                    String state = resultSet.getString("state");
                    String country = resultSet.getString("country");

                    Hotel.Location location = new Hotel.Location(latitude, longitude);
                    return new Hotel(name, hotelId, street, city, state, country, location);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a list of reviews associated with a specific hotel based on the provided hotelId.
     *
     * @param hotelIdParam The unique identifier of the hotel for which to retrieve reviews.
     * @return List of Review objects representing the reviews for the specified hotel.
     */
    public List<Review> getReviewListById(String hotelIdParam) {
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_REVIEWS_BY_HOTEL_ID)) {
                statement.setString(1, hotelIdParam);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String reviewId = resultSet.getString("reviewId");
                    int ratingOverall = resultSet.getInt("ratingOverall");
                    String title = resultSet.getString("title");
                    String reviewText = resultSet.getString("reviewText");
                    String userNickname = resultSet.getString("userNickname");
                    String reviewSubmissionDate = resultSet.getString("reviewSubmissionDate");
                    Review review = new Review(Integer.parseInt(hotelIdParam), reviewId, ratingOverall, title, reviewText, userNickname, reviewSubmissionDate);
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getReviewListByUsername(String username) {
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_REVIEWS_BY_USERNAME)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String reviewId = resultSet.getString("reviewId");
                    String hotelId = resultSet.getString("hotelId");
                    int ratingOverall = resultSet.getInt("ratingOverall");
                    String title = resultSet.getString("title");
                    String reviewText = resultSet.getString("reviewText");
                    String userNickname = resultSet.getString("userNickname");
                    String reviewSubmissionDate = resultSet.getString("reviewSubmissionDate");
                    Review review = new Review(Integer.parseInt(hotelId), reviewId, ratingOverall, title, reviewText, userNickname, reviewSubmissionDate);
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    /**
     * Deletes a specific review from the "reviews" table based on the provided hotelId, reviewId, and username.
     *
     * @param hotelId  The unique identifier of the hotel associated with the review.
     * @param reviewId The unique identifier of the review to be deleted.
     * @param username The username (userNickname) associated with the review.
     */
    public void deleteReview(String hotelId, String reviewId, String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
            PreparedStatement deleteStatement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW)) {

            deleteStatement.setString(1, reviewId);
            deleteStatement.setString(2, hotelId);
            deleteStatement.setString(3, username);

            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Review deleted successfully");
            } else {
                System.out.println("Failed to delete review");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new review to the database.
     *
     * @param hotelId       The ID of the hotel for which the review is being added.
     * @param ratingOverall The overall rating given in the review.
     * @param title         The title of the review.
     * @param reviewText    The text content of the review.
     * @param username      The username of the user who is submitting the review.
     */
    public void addReview(String hotelId, String ratingOverall, String title, String reviewText, String username) {
        String reviewId = getNewReviewId();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_REVIEW);
            statement.setString(1, reviewId);
            statement.setString(2, hotelId);
            statement.setInt(3, Integer.parseInt(ratingOverall));
            statement.setString(4, title);
            statement.setString(5, reviewText);
            statement.setString(6, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Edits an existing review in the database.
     *
     * @param hotelId    The ID of the hotel to which the review belongs.
     * @param reviewId   The ID of the review being edited.
     * @param title      The new title for the review.
     * @param reviewText The new text content for the review.
     * @param rating     The new rating for the review.
     * @param username   The username of the user editing the review.
     */
    public void editReview(String hotelId, String reviewId, String title, String reviewText, String rating, String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.EDIT_REVIEW)) {
            statement.setString(1, title);
            statement.setString(2, reviewText);
            statement.setDouble(3, Double.parseDouble(rating));
            statement.setString(4, hotelId);
            statement.setString(5, reviewId);
            statement.setString(6, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Retrieves a review from the database based on hotel and review IDs.
     *
     * @param hotelId  The ID of the hotel associated with the review.
     * @param reviewId The ID of the review to be retrieved.
     * @return A Review object containing the details of the found review or null if no review is found.
     */
    public Review getReviewById(String hotelId, String reviewId) {
        Review review = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_REVIEW_BY_REVIEW_ID)) {

            statement.setString(1, hotelId);
            statement.setString(2, reviewId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int ratingOverall = resultSet.getInt("ratingOverall");
                String title = resultSet.getString("title");
                String reviewText = resultSet.getString("reviewText");
                String userNickname = resultSet.getString("userNickname");
                String reviewSubmissionDate = resultSet.getString("reviewSubmissionDate");

                review = new Review(Integer.parseInt(reviewId), hotelId, ratingOverall, title, reviewText, userNickname, reviewSubmissionDate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return review;
    }

    /**
     * Generates a new unique review ID.
     *
     * @return A unique string identifier for a new review.
     */
    private String getNewReviewId() {
        UUID uuid = UUID.randomUUID();
        String reviewId = uuid.toString().replace("-", "").substring(0, 32);
        return reviewId;
    }

    /**
     * Retrieves a limited number of reviews for a specified hotel with pagination support.
     */
    public List<Review> getReviewWithLimit(String hotelId, int limit, int offSet) {
        PreparedStatement statement;
        List<Review> reviewList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_REVIEWS_WITH_FIXED_NUMBER);
            statement.setString(1, hotelId);
            statement.setInt(2, limit);
            statement.setInt(3, offSet);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String reviewId = resultSet.getString("reviewId");
                int ratingOverall = resultSet.getInt("ratingOverall");
                String title = resultSet.getString("title");
                String reviewText = resultSet.getString("reviewText");
                String userNickname = resultSet.getString("userNickname");
                String reviewSubmissionDate = resultSet.getString("reviewSubmissionDate");
                Review review = new Review(Integer.parseInt(hotelId), reviewId, ratingOverall, title, reviewText, userNickname, reviewSubmissionDate);
                reviewList.add(review);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return reviewList;
    }

    /**
     * Creates the favoriteHotel table if it does not already exist.
     */
    public void createFavoriteTable() {
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            List<String> allTableNames = getTablesName();
            if (!allTableNames.contains("favoriteHotel")) {
                try (Statement statement = dbConnection.createStatement()) {
                    statement.executeUpdate(PreparedStatements.CREATE_FAVORITE_TABLE);
                    System.out.println("favoriteHotel table created successfully");
                } catch (SQLException createTableEx) {
                    System.out.println("Error creating user table: " + createTableEx);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex);
        }
    }

    /**
     * Clears the favorite list for a specified user.
     */
    public void clearFavoriteList(String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.CLEAR_FAVORITE);
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Adds a hotel to the favorites list for a user and returns true if operation fails.
     */
    public boolean addFavorites(String username, String hotelId, String hotelName) {
        boolean operationFailed = false;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_TO_FAVORITE_TABLE);
            statement.setString(1, username);
            statement.setString(2, hotelName);
            statement.setString(3, hotelId);
            statement.executeUpdate();
        } catch (SQLException e) {
            operationFailed = true;
            System.out.println(e);
        }
        return operationFailed;
    }

    /**
     * Retrieves a list of favorite hotels for a specified user.
     */
    public List<String> getFavoriteHotels(String username) {
        List<String> hotelNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_FAVORITE_HOTELS);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                hotelNames.add(results.getString("hotelName"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return hotelNames;
    }

    /**
     * Creates a new table for storing favorite hotels if it doesn't exist.
     */
    public void createHistoryTable() {
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            List<String> allTableNames = getTablesName();
            if (allTableNames.contains("favoriteHotel")) {
                return;
            }
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(PreparedStatements.CREATE_HISTORY_TABLE);
                System.out.println("favoriteHotel table created successfully");
            } catch (SQLException createTableEx) {
                System.out.println("Error creating favoriteHotel table: " + createTableEx);
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex);
        }
    }

    /**
     * Stores a link in the user's history.
     */
    public void storeLink(String username, String link) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_TO_HISTORY_TABLE);
            statement.setString(1, username);
            statement.setString(2, link);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Clears the history of links for a specific user.
     */
    public void clearExpediaHistory(String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.CLEAR_HISTORY);
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Retrieves the history of links for a specific user.
     */
    public List<String> getLinkHistory(String username) {
        List<String> linkList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_HISTORY);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                linkList.add(results.getString("Link"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return linkList;
    }

    /**
     * Creates a new table for storing last login information if it doesn't exist.
     */
    public void createLastLoginTable() {
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            List<String> allTableNames = getTablesName();
            if (allTableNames.contains("lastlogin")) {
                return;
            }
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(PreparedStatements.CREATE_LAST_LOGIN_TABLE);
                System.out.println("lastlogin table created successfully");
            } catch (SQLException createTableEx) {
                System.out.println("Error creating lastlogin table: " + createTableEx);
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex);
        }
    }

    /**
     * Retrieves the last login date and time for a specified user.
     */
    public String getLastLoginTime(String username) {
        String lastLoginDateTime = "";
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_LAST_LOGIN);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                lastLoginDateTime = results.getString("DateandTime");
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching last login: " + ex);
        }
        return lastLoginDateTime;
    }

    /**
     * Checks if a login time exists for a specified user.
     */
    public boolean checkTimeExist(String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.CHECK_TIME_EXIST);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            return results.next();
        } catch (SQLException ex) {
            System.out.println("Error checking time existence: " + ex);
            return false;
        }
    }

    /**
     * Returns the current date in yyyy-MM-dd format.
     */
    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Returns the current time in HH:mm format.
     */
    public static String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Updates the last login time for a specified user.
     */
    public void updateLastLoginTable(String username) {
        String loginTime = getCurrentTime() + "," + getCurrentDate();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.UPDATE_LAST_LOGIN);
            statement.setString(1, loginTime);
            statement.setString(2, username);
            statement.executeUpdate();
//            System.out.println("Last login updated successfully.");
        } catch (SQLException ex) {
            System.out.println("Error updating last login: " + ex);
        }
    }

    /**
     * Adds a new last login time entry for a specified user.
     */
    public void UpdateCurrentTime(String username) {
        String loginTime = getCurrentTime() + ", " + getCurrentDate();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.UPDATE_CURRENT_TIME);
            statement.setString(1,loginTime);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error adding user to last login table: " + ex);
        }
    }

    public void AddCurrentTime(String username) {
        String loginTime = getCurrentTime() + ", " + getCurrentDate();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.INSERT_CURRENT_TIME);
            statement.setString(1, username);
            statement.setString(2, loginTime);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error adding user to last login table: " + ex);
        }
    }

    public void UpdateLastTime(String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            String selectSql = "SELECT Currenttime FROM lastlogin WHERE Username = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                String currenttime = resultSet.getString("Currenttime");
                String updateSql = "UPDATE lastlogin SET DateandTime = ? WHERE Username = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, currenttime);
                updateStatement.setString(2, username);
                updateStatement.executeUpdate();
            } else {
                System.out.println("Username not found in lastlogin table");
            }
        } catch (SQLException ex) {
            System.out.println("Error updating DateandTime: " + ex);
        }
    }




}

