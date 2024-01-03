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

public class DeleteReviewServlet extends HttpServlet {

    /**
     * Handles GET requests for the registration page.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If there is an I/O error.
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

        Template template = ve.getTemplate("templates/deleteReview.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        out.println(writer);
    }

    /**
     * Handles POST requests for user registration.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If there is an I/O error.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        String reviewId = request.getParameter("reviewId");
        reviewId = StringEscapeUtils.escapeHtml4(reviewId);

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.deleteReview(hotelId, reviewId, username);
        response.sendRedirect("/hotelInfo?hotelId=" + hotelId);
    }

}
