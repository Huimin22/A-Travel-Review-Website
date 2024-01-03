package server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class LogoutServlet extends HttpServlet{

    /**
     * Handles HTTP GET requests for logging out a user.
     *
     * @param request  the HttpServletRequest object containing the request parameters
     * @param response the HttpServletResponse object for sending the response
     * @throws IOException if an I/O exception occurs while processing the request or response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = (String)session.getAttribute("username");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
//        boolean timeExist = dbHandler.checkTimeExist(username);
//        if (timeExist) {
//            dbHandler.updateLastLoginTable(username);
//        } else {
//            dbHandler.UpdateCurrentTime(username);
//        }

        dbHandler.UpdateLastTime(username);

        request.getSession().invalidate();
        response.sendRedirect("/login");
    }

}
