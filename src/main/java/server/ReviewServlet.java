package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Review;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ReviewServlet extends HttpServlet {
    public static final int LIMIT = 8;

    /**
     * Handles GET requests to retrieve a list of reviews for a hotel with pagination.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        String isNext = request.getParameter("isNext");
        String isOnClick = request.getParameter("isOnClick");
        String hotelId = request.getParameter("hotelId");
        HttpSession session = request.getSession();
        int offset = calculateOffset(session, isOnClick);
        System.out.println(offset);
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        List<Review> reviewList = databaseHandler.getReviewWithLimit(hotelId, LIMIT, offset);

        if (reviewList.size() == 0 && isNext.equals("false")) {
            // If no reviews and the user goes to the previous page, update the offset to move backwards
            session.setAttribute("offset", offset - LIMIT);
        }
        if (reviewList.size() != 0) {
            if ("false".equals(isOnClick)) {
                if (offset > 0 && "false".equals(isNext)) {
                    //If the user wants to go previous reviews, update offset
                    session.setAttribute("offset", offset - LIMIT);
                    offset = offset - LIMIT;
                    reviewList = databaseHandler.getReviewWithLimit(hotelId, LIMIT, offset);
                } else if (reviewList.size() == LIMIT && "true".equals(isNext)) {
                    //If the user wants to go next reviews, update offset
                    session.setAttribute("offset", offset + LIMIT);
                }
            } else {
                // If the user has initiated a new click, reset the offset to start from the first page
                offset = 0;
                session.setAttribute("offset", offset);
            }
        }
        JsonObject jsonObject = buildJsonResponse(reviewList, hotelId);
        PrintWriter out = response.getWriter();
        out.println(jsonObject);
    }

    /**
     * Calculate the offset based on the HttpSession and onClickFlag.
     *
     * @param session      The HttpSession object.
     * @param onClickFlag  A flag indicating whether the user initiated a new click.
     * @return The calculated offset value.
     */
    private int calculateOffset(HttpSession session, String onClickFlag) {
        Object offsetObject = session.getAttribute("offset");
        if (offsetObject == null) {
            return 0;
        }
        int offsetValue = (int) offsetObject;
        if (offsetValue == 0 && "false".equals(onClickFlag)) {
            return LIMIT;
        }
        return offsetValue;
    }

    /**
     * Build a JSON response object from a list of reviews and hotel ID.
     *
     * @param reviewList The list of reviews to include in the JSON response.
     * @param hotelId    The ID of the hotel associated with the reviews.
     * @return A JsonObject representing the JSON response.
     */
    private JsonObject buildJsonResponse(List<Review> reviewList, String hotelId) {
        JsonObject jsonObject = new JsonObject();
        if (reviewList == null || reviewList.isEmpty()) {
            jsonObject.addProperty("hotelId", "invalid");
            return jsonObject;
        }

        JsonArray jsonArray = new JsonArray();
        jsonObject.addProperty("hotelId", hotelId);
        for (Review review : reviewList) {
            JsonObject jsonReview = new JsonObject();
            jsonReview.addProperty("reviewId", review.getReviewId());
            jsonReview.addProperty("title", review.getTitle());
            jsonReview.addProperty("user", review.getUserNickname());
            jsonReview.addProperty("reviewText", review.getReviewText());
            jsonReview.addProperty("date", review.getDatePosted().toString());
            jsonArray.add(jsonReview);
        }
        jsonObject.add("reviews", jsonArray);
        return jsonObject;
    }

}
