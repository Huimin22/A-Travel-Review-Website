package server;

import hotelapp.Hotel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class WeatherServlet extends HttpServlet {
    /**
     * Handles a GET request to retrieve weather data for a hotel and sends it as a JSON response.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("/login");
            return;
        }

        PrintWriter out = response.getWriter();
        String hotelId = request.getParameter("hotelId");
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        Hotel hotel = databaseHandler.getHotelById(hotelId);

        WeatherFetch weatherFetch = new WeatherFetch();
        String jsonObject = weatherFetch.getWeatherData(hotel);
        out.println(jsonObject);
    }
}
