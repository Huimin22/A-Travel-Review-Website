package server;

import hotelapp.Hotel;
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
import java.util.ArrayList;
import java.util.List;

public class SearchHotelServlet extends HttpServlet {

    /**
     * Handles the HTTP GET request.
     * @param request  The HttpServletRequest object that contains the request the client made to the servlet.
     * @param response The HttpServletResponse object that contains the response the servlet sends to the client.
     * @throws IOException If an input or output error is detected when the servlet handles the GET request.
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
        // Get hotels from the session and put them in the context
        List<Hotel> resHotels = (List<Hotel>) session.getAttribute("resHotels");

        context.put("resHotels", resHotels);
        context.put("servlet", request.getServletPath());
        // Merge the template with the context
        Template template = ve.getTemplate("templates/searchHotel.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Output the merged template
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String keyword = request.getParameter("hotelname");
        keyword = StringEscapeUtils.escapeHtml4(keyword);
        HttpSession session = request.getSession();
        // Perform hotel search and store the result in the session

        List<Hotel> resHotels = performHotelSearch(keyword);
        session.setAttribute("resHotels", resHotels);
        response.sendRedirect("/searchHotel");
    }

    private List<Hotel> performHotelSearch(String keyword) {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        List<Hotel> allHotels = dbHandler.getAllHotels();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allHotels;
        }

        List<Hotel> matchingHotels = new ArrayList<>();
        for (Hotel hotel : allHotels) {
            if (hotel.getName().toLowerCase().contains(keyword.toLowerCase())) {
                matchingHotels.add(hotel);
            }
        }
        return matchingHotels;
    }


}
