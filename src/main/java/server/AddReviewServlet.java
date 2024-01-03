package server;

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

public class AddReviewServlet extends HttpServlet {

    /**
     * Handles GET requests for editing an existing review.
     * Redirects to the registration page if the user is not logged in.
     * Retrieves review details and renders the editReview.html template.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
            return;
        }

        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        String hotelId = request.getParameter("hotelId");

        context.put("hotelId", hotelId);
        context.put("servlet", request.getServletPath());
        Template template = ve.getTemplate("templates/addReview.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    /**
     * Handles POST requests for updating a review.
     * Retrieves and sanitizes updated parameters, updates the database,
     * and redirects to the hotelInfo page.
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

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.addReview(hotelId, rating, title, reviewText, username);
        response.sendRedirect("/hotelInfo?hotelId=" + hotelId);
    }

}
