package hotelapp;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeHotelData extends HotelReviewData {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ThreadSafeHotelData() {
        super();
    }

    /**
     * Add a list of hotels to the data while acquiring a write lock.
     *
     * @param hotels The list of hotels to add.
     */
    @Override
    public void addHotels(List<Hotel> hotels) {
        try {
            lock.writeLock().lock();
            super.addHotels(hotels);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add a list of reviews to the data while acquiring a write lock.
     *
     * @param hotelReview The list of reviews to add.
     */
    @Override
    public void addReviews(List<Review> hotelReview) {
        try {
            lock.writeLock().lock();
            super.addReviews(hotelReview);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Build the word map while acquiring write lock.
     */
    @Override
    public void buildWordMap() {
        try {
            lock.writeLock().lock();
            super.buildWordMap();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Find a specific word in the data while acquiring a read lock.
     *
     * @param word The word to search for.
     * @return The information related to the word.
     */
    @Override
    public String findWord(String word) {
        try {
            lock.readLock().lock();
            return super.findWord(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Find hotel information by a specific ID while acquiring a read lock.
     *
     * @param targetHotelId The ID of the hotel to search for.
     * @return The information related to the hotel.
     */
    @Override
    public String findHotelInfo(String targetHotelId) {
        try {
            lock.readLock().lock();
            return super.findHotelInfo(targetHotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Find review information by a specific hotel ID while acquiring a read lock.
     *
     * @param targetHotelId The ID of the hotel for which to find reviews.
     * @return The information related to the reviews of the hotel.
     */
    @Override
    public String findReviewInfo(int targetHotelId) {
        try {
            lock.readLock().lock();
            return super.findReviewInfo(targetHotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

}
