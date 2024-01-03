package server;

import org.apache.commons.text.StringEscapeUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class AddFavoritesServlet extends HttpServlet {

    /**
     * Processes GET request to add a hotel to the user's favorites, redirecting to login if the user is not authenticated.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        boolean isError = databaseHandler.addFavorites(username, hotelId, hotelName);
        System.out.println(isError);
        out.println(isError);
    }

}
