package server;

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
import java.util.List;

public class EditReviewServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for displaying the page to edit a review.
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

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String reviewId = request.getParameter("reviewId");
        reviewId = StringEscapeUtils.escapeHtml4(reviewId);

        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        List<Review> reviews = dbHandler.getReviewListByUsername(username);

        context.put("servlet", request.getServletPath());

        context.put("reviews", reviews);
        context.put("hotelId", hotelId);
        context.put("reviewId", reviewId);


        Template template = ve.getTemplate("templates/editReview.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        out.println(writer);
    }

    /**
     * This method is responsible for processing form submissions, extracting and escaping HTML characters from request parameters,
     * and updating the review information in the database.
     *
     * @param request  the HttpServletRequest object containing the form data
     * @param response the HttpServletResponse object for sending the response
     * @throws IOException if an I/O exception occurs while processing the request or response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("reviewTitle");
        title = StringEscapeUtils.escapeHtml4(title);
        String reviewText = request.getParameter("reviewContent");
        reviewText = StringEscapeUtils.escapeHtml4(reviewText);
        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String rating = request.getParameter("rating");
        rating = StringEscapeUtils.escapeHtml4(rating);
        String reviewId = request.getParameter("reviewId");
        reviewId = StringEscapeUtils.escapeHtml4(reviewId);

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.editReview(hotelId, reviewId, title, reviewText, rating, username);
        response.sendRedirect("/hotelInfo?hotelId=" + hotelId);
    }


}
