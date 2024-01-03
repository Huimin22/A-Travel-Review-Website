package hotelapp;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Review implements Comparable<Review>{
    private final int hotelId;
    private final String reviewId;
    private final double ratingOverall;
    private final String title;
    private final String reviewText;
    private final String userNickname;
    @SerializedName(value = "reviewSubmissionTime")
    private final String datePosted;

    /**
     * Constructor to create a Review object.
     * @param hotelId The unique identifier of the hotel associated with the review.
     * @param reviewId The unique identifier of the review.
     * @param ratingOverall The overall rating of the review.
     * @param title The title of the review.
     * @param reviewText The text of the review.
     * @param userNickname The nickname of the user who posted the review.
     * @param datePosted The date and time when the review was posted.
     */
    public Review(int hotelId, String reviewId, int ratingOverall, String title, String reviewText, String userNickname, String datePosted) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.ratingOverall = ratingOverall;
        this.title = title;
        this.reviewText = reviewText;
        this.userNickname = userNickname;
        this.datePosted = datePosted;
    }

    /**
     * Get the unique identifier of the hotel associated with the review.
     * @return The hotel ID.
     */
    public int getHotelId() {
        return hotelId;
    }

    /**
     * Get the unique identifier of the review.
     * @return The review ID.
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Get the overall rating of the review.
     * @return The overall rating.
     */
    public double getRatingOverall() {
        return ratingOverall;
    }

    /**
     * Get the title of the review.
     * @return The review title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the text of the review.
     * @return The review text.
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Get the nickname of the user who posted the review.
     * @return The user's nickname.
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * Get the date and time when the review was posted.
     * @return The date and time of posting.
     */
    public String getDatePosted() {
        return datePosted;
    }

    @Override
    public int compareTo(Review r) {
        return this.getReviewId().compareTo(r.getReviewId());
    }

    /**
     * Process the review text to remove common words and special characters.
     * @param review The review to process.
     * @return An array of cleaned words from the review text.
     */
    public String[] processReview(Review review) {
        String[] commonWords = {"a", "the", "is", "are", "were", "and"};
        String reviewText = review.getReviewText();
        String[] words = reviewText.split("\\s+");
        StringBuilder cleanedReview = new StringBuilder();

        for (String word : words) {
            word = word.toLowerCase();
            word = word.replaceAll("[^a-zA-Z]", "");

            if (!Arrays.asList(commonWords).contains(word) && !word.isEmpty()) {
                cleanedReview.append(word).append(" ");
            }
        }
        return cleanedReview.toString().trim().split("\\s+");
    }

}

