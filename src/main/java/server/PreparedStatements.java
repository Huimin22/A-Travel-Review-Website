package server;

public class PreparedStatements {
    /** Prepared Statements  */
    /** For creating the users table */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    /** For creating the hotels table */
    public static final String CREATE_HOTELS_TABLE =
            "CREATE TABLE hotels (" +
                    "hotelID VARCHAR(32) PRIMARY KEY, " +
                    "name VARCHAR(32) NOT NULL, " +
                    "latitude DOUBLE NOT NULL, " +
                    "longitude DOUBLE NOT NULL, " +
                    "street VARCHAR(32) NOT NULL, " +
                    "city VARCHAR(32) NOT NULL, " +
                    "state VARCHAR(32) NOT NULL, " +
                    "country VARCHAR(32) NOT NULL);";

    /** For creating the reviews table */
    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE reviews (" +
                    "reviewId VARCHAR(32) PRIMARY KEY, " +
                    "hotelId VARCHAR(32), FOREIGN KEY (hotelID) references hotels (hotelID), " +
                    "ratingOverall DOUBLE, " +
                    "title VARCHAR(100), " +
                    "reviewText VARCHAR(3000), " +
                    "userNickname VARCHAR(32), " +
                    "reviewSubmissionDate VARCHAR(32)); ";

    /** Used to insert a new user into the database. */
    public static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";

    /** Used to retrieve the salt associated with a specific user. */
    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?";

    /** Used to authenticate a user. */
    public static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ?";

    /**
     * SQL query to retrieve a list of all tables.
     */
    public static final String SHOW_TABLES = "show tables;";

    /**
     * SQL query to add all hotels to the "hotels" table.
     */
    public static final String ADD_ALL_HOTELS_TO_TABLE =
            "INSERT INTO hotels (hotelID, name, latitude, longitude, street, city, state, country) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * SQL query to add all reviews to the "reviews" table.
     */
    public static final String ADD_ALL_REVIEWS_TO_TABLE =
            "INSERT INTO reviews (reviewId, hotelId, ratingOverall, title, reviewText, userNickname, reviewSubmissionDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    /**
     * SQL query to retrieve all hotels from the "hotels" table.
     */
    public static final String GET_ALL_HOTELS = "SELECT * FROM hotels";

    /**
     * SQL query to retrieve all usernames from the "users" table.
     */
    public static final String GET_ALL_USERS = "SELECT username FROM users";

    /**
     * SQL query to retrieve a hotel by its ID from the "hotels" table.
     */
    public static final String GET_HOTEL_BY_ID = "SELECT * FROM hotels WHERE hotelID = ?";

    /**
     * SQL query to retrieve reviews by hotel ID from the "reviews" table.
     */
    public static final String GET_REVIEWS_BY_HOTEL_ID = "SELECT * FROM reviews WHERE hotelId = ?";
    public static final String GET_REVIEWS_BY_USERNAME = "SELECT * FROM reviews WHERE userNickname = ?";

    /**
     * SQL query to delete a review from the "reviews" table based on review ID, hotel ID, and user nickname.
     */
    public static final String DELETE_REVIEW = "DELETE FROM reviews WHERE reviewId = ? AND hotelId = ? AND userNickname = ?";

    /**
     * SQL query to retrieve a review by review ID and hotel ID from the "reviews" table.
     */
    public static final String GET_REVIEW_BY_REVIEW_ID = "SELECT * FROM reviews WHERE hotelId = ? AND reviewId = ?";

    /**
     * SQL query to edit a review in the "reviews" table based on hotel ID, review ID, and user nickname.
     */
    public static final String EDIT_REVIEW =
            "UPDATE reviews SET title = ?, reviewText = ?, ratingOverall = ?, reviewSubmissionDate = CURRENT_TIMESTAMP " +
                    "WHERE hotelId = ? AND reviewId = ? AND userNickname = ?";

    /**
     * SQL query to add a review to the "reviews" table.
     */
    public static final String ADD_REVIEW = "INSERT INTO reviews (reviewId, hotelId, ratingOverall, title, reviewText, userNickname, reviewSubmissionDate)" +
        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";

    public static final String GET_REVIEWS_WITH_FIXED_NUMBER = "select * from reviews where hotelId=? order by reviewSubmissionDate desc limit ? offset ?;";


    public static final String CREATE_FAVORITE_TABLE =
            "create table favoriteHotel(Username VARCHAR(64), hotelName VARCHAR(200), hotelId VARCHAR(64), PRIMARY KEY(hotelId, Username));";

    public static final String ADD_TO_FAVORITE_TABLE =
            "insert into favoriteHotel(Username, hotelName, hotelId) " +
                    "values(?,?,?);";

    public static final String GET_FAVORITE_HOTELS =
            "select hotelName from favoriteHotel where Username =?;";

    public static final String CLEAR_FAVORITE =
            "delete from favoriteHotel where Username =?;";

    public static final String CREATE_HISTORY_TABLE =
            "create table expediaHistory(Username VARCHAR(32), Link VARCHAR(100));";

    public static final String ADD_TO_HISTORY_TABLE = "insert into expediaHistory(Username, Link) " +
            "values(?,?);";

    public static final String CLEAR_HISTORY =
            "delete from expediaHistory where Username =?;";


    public static final String GET_HISTORY =
            "select link from expediaHistory where Username =?;";


    public static final String CREATE_LAST_LOGIN_TABLE =
            "create table lastlogin(Username VARCHAR(32), DateandTime VARCHAR(32));";


    public static final String GET_LAST_LOGIN =
            "select DateandTime from lastlogin where Username =?;";


    public static final String CHECK_TIME_EXIST =
            "SELECT DateandTime FROM lastlogin WHERE username = ?;";

    public static final String UPDATE_LAST_LOGIN =
            "update lastlogin set DateandTime= ? where Username=?;";


    public static final String UPDATE_CURRENT_TIME =
            "update lastlogin set Currenttime = ? WHERE Username = ?;";

    public static final String INSERT_CURRENT_TIME =
            "INSERT INTO lastlogin (Username, Currenttime) VALUES (?, ?);";
}
