package hotelapp;

import java.time.LocalDate;
import java.util.*;

public class HotelReviewData {
    private TreeMap<String, Hotel> hotelMap;
    private Map<Integer, TreeSet<Review>> reviewMap;
    private Map<String, TreeSet<Map.Entry<Review, Integer>>> wordMap;

    /**
     * Constructor for the HotelReviewData class.
     * Initializes three HashMap objects: hotelMap, reviewMap, and wordMap,
     * which are used to store data related to hotel reviews.
     */
    public HotelReviewData() {
        this.hotelMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String hotelId1, String hotelId2) {
                return hotelId1.compareTo(hotelId2);
            }
        });
        this.reviewMap = new HashMap<>();
        this.wordMap = new HashMap<>();
    }

    /**
     * Retrieves the map of hotels where the keys are hotel IDs and the values are Hotel objects.
     *
     * @return A TreeMap containing hotel data.
     */
    public TreeMap<String, Hotel> getHotelMap() {
        return hotelMap;
    }

    /**
     * Retrieves the map of reviews where the keys are hotel IDs and the values are sorted sets of Review objects.
     *
     * @return A Map containing review data, organized by hotel IDs.
     */
    public Map<Integer, TreeSet<Review>> getReviewMap() {
        return reviewMap;
    }

    /**
     * Retrieves a hotel by its unique identifier.
     *
     * @param hotelId The identifier of the hotel to retrieve.
     * @return The Hotel object associated with the provided ID, or null if the ID is null.
     */
    public Hotel getHotelById(String hotelId) {
        if (hotelId != null) {
            return hotelMap.get(hotelId);
        } else {
            return null;
        }
    }

    /**
     * Retrieves the set of reviews associated with a specific hotel ID.
     *
     * @param hotelId The identifier of the hotel for which to retrieve reviews.
     * @return A set of Review objects related to the provided hotel ID, or null if the ID is null.
     */
    public Set<Review> getReviewsByHotelId(String hotelId) {
        if (hotelId != null) {
            TreeSet<Review> reviews = reviewMap.get(Integer.parseInt(hotelId));
            return reviews;
        } else {
            return null;
        }
    }

    /**
     * Retrieves reviews containing a specific word and the frequency of each occurrence.
     *
     * @param word The word to search for in reviews.
     * @return A set of Map Entries, where each entry consists of a Review and its frequency for the provided word,
     *         or null if the word is null.
     */
    public Set<Map.Entry<Review, Integer>> getReviewsByWord(String word) {
        if (word != null) {
            TreeSet<Map.Entry<Review, Integer>> reviews = wordMap.get(word);
            return reviews;
        }
        else {
            return null;
        }
    }

    /**
     * Adds a list of hotels to the internal hotelMap.
     *
     * @param hotels A list of Hotel objects to be added to the internal data structure.
     */
    public void addHotels(List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            this.hotelMap.put(hotel.getHotelId(), hotel);
        }
    }

    /**
     * Adds the list of hotel reviews with the same hotelId to the reviewMap.
     * If the reviewMap already contains reviews for the same hotelId, the new reviews are added to the existing set.
     * If the hotelId is not already in the reviewMap, a new TreeSet of reviews is created and added for that hotelId.
     *
     * @param hotelReview A list of Review objects to be added to the reviewMap.
     */
    public void addReviews(List<Review> hotelReview) {
        if (!hotelReview.isEmpty()) {
            int hotelId = hotelReview.get(0).getHotelId();
            if (reviewMap.containsKey(hotelId)) {
                TreeSet<Review> existingReviews = reviewMap.get(hotelId);
                existingReviews.addAll(hotelReview);
            } else {
                TreeSet<Review> newReviews = new TreeSet<>(new Comparator<Review>() {
                    @Override
                    public int compare(Review r1, Review r2) {
                        LocalDate date1 = LocalDate.parse(r1.getDatePosted().substring(0, 10));
                        LocalDate date2 = LocalDate.parse(r2.getDatePosted().substring(0, 10));
                        int dateComparison = date2.compareTo(date1);
                        if (dateComparison != 0) {
                            return dateComparison;
                        }
                        return r1.getReviewId().compareTo(r2.getReviewId());
                    }

                });
                newReviews.addAll(hotelReview);
                reviewMap.put(hotelId, newReviews);
            }
        }
    }

    /**
     * Builds a word map based on the reviews in the reviewMap.
     */
    public void buildWordMap() {
        for (TreeSet<Review> reviews : reviewMap.values()) {
            for (Review review : reviews) {
                //Process review text
                String[] cleanWords = review.processReview(review);
                // Count the frequency of each word in the review
                WordCounter wordCounter = new WordCounter();
                Map<String, Integer> wordFrequencyMap = wordCounter.countWordFrequency(cleanWords);
                // Update the inverted index with the review and its word frequencies
                for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
                    String word = entry.getKey();
                    int frequency = entry.getValue();
                    Map.Entry<Review, Integer> reviewFrequency = new AbstractMap.SimpleEntry<>(review, frequency);
                    if (wordMap.containsKey(word)) {
                        wordMap.get(word).add(reviewFrequency);
                    } else {
                        TreeSet<Map.Entry<Review, Integer>> reviewSet = new TreeSet<>(new Comparator<Map.Entry<Review, Integer>>() {
                            @Override
                            public int compare(Map.Entry<Review, Integer> s1, Map.Entry<Review, Integer> s2) {
                                int valueComparison = s2.getValue().compareTo(s1.getValue());
                                if (valueComparison != 0) {
                                    return valueComparison;
                                }
                                LocalDate date1 = LocalDate.parse(s1.getKey().getDatePosted().substring(0, 10));
                                LocalDate date2 = LocalDate.parse(s2.getKey().getDatePosted().substring(0, 10));
                                int dateComparison = date2.compareTo(date1);
                                if (dateComparison != 0) {
                                    return dateComparison;
                                }
                                return s1.getKey().getReviewId().compareTo(s2.getKey().getReviewId());
                            }

                        });
                        reviewSet.add(reviewFrequency);
                        wordMap.put(word, reviewSet);
                    }
                }
            }
        }
    }

    /**
     * Finds and returns all reviews containing a specific word.
     *
     * @param word The word to search for in reviews.
     * @return A formatted string containing information about reviews with the specified word.
     */
    public String findWord(String word) {
        if (!wordMap.containsKey(word)) {
            return "Word: " + word + " not found.";
        }
        TreeSet<Map.Entry<Review, Integer>> wordSet = wordMap.getOrDefault(word, new TreeSet<>(Comparator.comparingInt(Map.Entry::getValue)));
        StringBuilder allWordReview = new StringBuilder();
        for (Map.Entry<Review, Integer> entry : wordSet) {
            Review review = entry.getKey();
            allWordReview.append("hotelId = " + review.getHotelId()).append("\n");
            allWordReview.append("reviewId = " + review.getReviewId()).append("\n");
            allWordReview.append("averageRating = " + review.getRatingOverall()).append("\n");
            allWordReview.append("title = " + review.getTitle()).append("\n");
            allWordReview.append("reviewText = " + review.getReviewText()).append("\n");
            allWordReview.append("userNickname = " + review.getUserNickname()).append("\n");
            allWordReview.append("submissionDate = " + review.getDatePosted()).append("\n");
            allWordReview.append("********************").append("\n");
        }
        return allWordReview.toString();
    }

    /**
     * Finds and returns information about a hotel with a specified ID.
     *
     * @param targetHotelId The ID of the hotel to search for.
     * @return Information about the hotel or an error message if the hotel is not found.
     */
    public String findHotelInfo(String targetHotelId) {
        if (hotelMap.containsKey(targetHotelId)) {
            Hotel targetHotel = hotelMap.get(targetHotelId);
            StringBuilder hotelInfo = new StringBuilder();
            hotelInfo.append("hotelName = ").append(targetHotel.getName()).append("\n");
            hotelInfo.append("hotelId = ").append(targetHotel.getHotelId()).append("\n");
            hotelInfo.append("latitude = ").append(targetHotel.getLatitude()).append("\n");
            hotelInfo.append("longtitude = ").append(targetHotel.getLongitude()).append("\n");
            hotelInfo.append("address = ").append(targetHotel.getFullAddress()).append("\n");
            return hotelInfo.toString();
        } else {
            System.out.println("Hotel with id " + targetHotelId + " not found.");
            return null;
        }
    }

    /**
     * Finds and returns information about all reviews for a hotel with a specified ID.
     *
     * @param targetHotelId The ID of the hotel for which to retrieve reviews.
     * @return Information about all reviews for the specified hotel.
     */
    public String findReviewInfo(int targetHotelId) {
        StringBuilder allReview = new StringBuilder();
        if (reviewMap.containsKey(targetHotelId)) {
            TreeSet<Review> targetReviewList = reviewMap.get(targetHotelId);
            for (Review review : targetReviewList) {
                if (review != null) {
                    allReview.append("hotelId  = ").append(review.getHotelId()).append("\n");
                    allReview.append("reviewId = ").append(review.getReviewId()).append("\n");
                    allReview.append("averageRating = ").append(review.getRatingOverall()).append("\n");
                    allReview.append("title = ").append(review.getTitle()).append("\n");
                    allReview.append("reviewText = ").append(review.getReviewText()).append("\n");
                    allReview.append("userNickname = ").append(review.getUserNickname()).append("\n");
                    allReview.append("submissionDate = ").append(review.getDatePosted()).append("\n");
                    allReview.append("********************").append("\n");
                }
            }
        } else {
            System.out.println("Hotel ID " + targetHotelId + " not found in the reviewMap.");
        }
        return allReview.toString();
    }


}
