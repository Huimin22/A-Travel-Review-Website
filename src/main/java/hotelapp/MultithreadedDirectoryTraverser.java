package hotelapp;

import server.DatabaseHandler;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * MultithreadedDirectoryTraverser is a class for efficiently traversing directories,
 * parsing JSON review files, and loading hotel and review data in a multithreaded manner.
 */
public class MultithreadedDirectoryTraverser {
    private ThreadSafeHotelData threadSafeHotelData = new ThreadSafeHotelData();
    private ExecutorService executor;
//    private Logger logger = LogManager.getLogger();
    private Phaser phaser = new Phaser();

    /**
     * Constructor to create an instance of MultithreadedDirectoryTraverser with a specified number of threads.
     *
     * @param numOfThread The number of threads in the fixed thread pool.
     */
    public MultithreadedDirectoryTraverser(Integer numOfThread) {
        executor = Executors.newFixedThreadPool(numOfThread);
    }

    /**
     * Private inner class for worker threads responsible for parsing individual JSON review files.
     */
    private class Worker implements Runnable {
        private String filePath;

        /**
         * Constructor for the Worker class.
         *
         * @param filePath The path of the JSON review file to be processed.
         */
        public Worker(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                List<Review> reviews = JsonProcessor.parseSingleReviewFile(filePath);
                threadSafeHotelData.addReviews(reviews);
//                DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
//                databaseHandler.addAllReviewsToTable(reviews);
//                logger.debug("Worker working on " + filePath + " finished work");
            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /**
     * Process a directory for JSON review files, including subdirectories.
     *
     * @param dir The path of the directory to be processed.
     */
    public void processReviewDirectory(Path dir) {
        try (DirectoryStream<Path> filesList = Files.newDirectoryStream(dir)) {
                for (Path path : filesList) {
                    if (Files.isDirectory(path)) {
                        processReviewDirectory(path);
                    } else if (path.toString().endsWith(".json")) {
                            executor.submit(new Worker(path.toString()));
                            phaser.register();
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
    }

    /**
     * Load hotel and review data from specified files and directories.
     *
     * @param hotelsFilePath  The path of the hotels data file (may be null if not provided).
     * @param reviewsFilePath The path of the root directory for review files (may be null if not provided).
     * @return The populated ThreadSafeHotelData instance containing the loaded data.
     */
    public ThreadSafeHotelData loadData(String hotelsFilePath, String reviewsFilePath) {
        if (!(hotelsFilePath == null)) {
            threadSafeHotelData.addHotels(JsonProcessor.parseHotelFile(hotelsFilePath));

        }
        if (!(reviewsFilePath == null)) {
            processReviewDirectory(Paths.get(reviewsFilePath));
            phaser.awaitAdvance(phaser.getPhase());
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        return threadSafeHotelData;
    }


}
