package server;

import hotelapp.Hotel;
import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.List;

public class HotelInfoServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for retrieving hotel information. This method is responsible
     * for processing requests, checking user authentication, fetching hotel details, reviews,
     * and average ratings, and rendering the information using a Velocity template.
     *
     * @param request  the HttpServletRequest object containing the request parameters
     * @param response the HttpServletResponse object for sending the response
     * @throws IOException if an I/O exception occurs while processing the request or response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("/login");
            return;
        }

        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        context.put("servlet", request.getServletPath());
        String hotelIdParam = request.getParameter("hotelId");
        hotelIdParam = StringEscapeUtils.escapeHtml4(hotelIdParam);

        if (hotelIdParam != null) {
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            try {
                Hotel hotel = dbHandler.getHotelById(hotelIdParam);
                List<Review> reviews = dbHandler.getReviewListById(hotelIdParam);
                if (hotel != null)
                    context.put("hotel", hotel);
                    Double avgRating = getAveRating(hotelIdParam);
                    String link = String.format("https://www.expedia.com/%s.h%s.Hotel-Information", hotel.getName(), hotel.getHotelId());
                    context.put("link", link);
                    context.put("avgRating", avgRating);
                    context.put("reviews", reviews);
                    context.put("username", username);

            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }

        // Merge the template with the context
        Template template = ve.getTemplate("templates/hotelInfo.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Output the merged template
        out.println(writer);
    }

    /**
     * Calculates and returns the average rating for a given hotel based on its reviews.
     *
     * @param hotelIdParam the hotel ID parameter used to retrieve reviews from the database
     * @return the average rating for the specified hotel, or 0.0 if no reviews are available
     */
    private Double getAveRating(String hotelIdParam) {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        List<Review> reviews = dbHandler.getReviewListById(hotelIdParam);
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double totalRating = 0.0;
        for (Review review : reviews) {
            totalRating += review.getRatingOverall();
        }
        double avgRating = totalRating / reviews.size();

        DecimalFormat df = new DecimalFormat("#.#");
        String formattedRating = df.format(avgRating);
        return  Double.parseDouble(formattedRating);
    }


}
