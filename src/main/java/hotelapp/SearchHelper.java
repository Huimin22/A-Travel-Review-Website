package hotelapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class SearchHelper {
    private HashMap<String, String> argMap = new HashMap<>();

    /**
     * Processes command line arguments to configure hotel search parameters.
     *
     * @param args The array of command line arguments.
     * @return A HashMap containing configured search parameters.
     */
    public void processArgs (String[] args) {
        if (args.length < 2) {
            System.out.println("Please configure.");
        }

        argMap.put("-threads", "1");

        for (int i = 0; i < args.length; i++) {
            if ("-hotels".equals(args[i]) && i + 1 < args.length) {
                argMap.put("-hotels", args[i + 1]);
            } else if ("-reviews".equals(args[i]) && i + 1 < args.length) {
                argMap.put("-reviews", args[i + 1]);
            } else if ("-threads".equals(args[i]) && i + 1 < args.length) {
                argMap.put("-threads", args[i + 1]);
            }
        }

//        if (!argMap.containsKey("-hotels")) {
//            System.out.println("Please configure: HotelSearch -hotels <hotels_file_path>");
//        }
    }

    /**
     * Get the value associated with a given argument name from a map.
     *
     * @param argName The name of the argument to retrieve.
     * @return The value associated with the argument name.
     */
    public String getArgValue(String argName) {
        return argMap.get(argName);
    }

    /**
     * Write hotel and review data to an output file in a specific format.
     *
     * @param hotelReviewData  The data structure containing hotel and review information.
     * @param outputFilePath   The path to the output file.
     * @param reviewsFilePath  The path to the directory containing review files (may be null).
     */
    public void writeFile (HotelReviewData hotelReviewData, String outputFilePath, String reviewsFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<String, Hotel> entry : hotelReviewData.getHotelMap().entrySet()) {
                String hotelId = entry.getKey();
                Hotel hotel = entry.getValue();
                writer.write("\n********************\n");
                writer.write(hotel.getName() + ": " + hotel.getHotelId() + "\n");
                writer.write(hotel.getAddress() + "\n");
                writer.write(hotel.getCityAndState() + "\n");
                if (!(reviewsFilePath == null)) {
                    if (hotelReviewData.getReviewMap().containsKey(Integer.parseInt(hotelId))) {
                        TreeSet<Review> reviewList = hotelReviewData.getReviewMap().get(Integer.parseInt(hotelId));
                        for (Review review : reviewList) {
                            if (review != null) {
                                writer.write("--------------------\n");
                                String reviewDate= review.getDatePosted();
                                String onlyDate = reviewDate.substring(0, reviewDate.indexOf("T"));
                                String userNickname = review.getUserNickname();
                                if (userNickname.equals("")) {
                                    userNickname = "Anonymous";
                                }
                                writer.write("Review by " + userNickname + " on " + onlyDate + "\n");
                                writer.write("Rating: " + (int)review.getRatingOverall() + "\n");
                                writer.write("ReviewId: " + review.getReviewId() + "\n");
                                writer.write(review.getTitle() + "\n");
                                writer.write(review.getReviewText() + "\n");
                            }

                        }
                    }
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle user input commands and interact with HotelReviewData.
     *
     * @param hotelReviewData The data structure containing hotel and review information.
     */
    public void handleUserInput(HotelReviewData hotelReviewData) {
        hotelReviewData.buildWordMap();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userInput = scanner.nextLine();
            if ("q".equalsIgnoreCase(userInput)) {
                break;
            } else if (userInput.trim().isEmpty()) {
                System.out.println("No command. Please enter a valid command.");
            } else if (userInput.matches("^find\\s+\\d+$")) {
                String[] parts = userInput.split("\\s+");
                String targetHotelId = parts[1];
                String result = hotelReviewData.findHotelInfo(targetHotelId);
                if (result != null) {
                    System.out.println(result);
                }
            } else if (userInput.matches("^findReviews\\s+\\d+$")) {
                String[] parts = userInput.split("\\s+");
                int targetHotelId = Integer.parseInt(parts[1]);
                String result = hotelReviewData.findReviewInfo(targetHotelId);
                if (result != null) {
                    System.out.println(result);
                }
            } else if (userInput.matches("^findWord\\s+\\w+$")) {
                String targetWord = userInput.split("\\s+")[1];
                String result = hotelReviewData.findWord(targetWord);
                if (result != null) {
                    System.out.println(result);
                }

            } else {
                System.out.println("Please enter valid command.");
            }

        }
        scanner.close();
    }

}
