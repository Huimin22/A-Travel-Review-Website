package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ClearFavoriteServlet extends HttpServlet {

    /**
     * Clears the favorite list of the authenticated user and redirects to the favorites display page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object object = session.getAttribute("username");
        if (object == null) {
            response.sendRedirect("/login");
            return;
        }
        String username = (String) object;
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.clearFavoriteList(username);
        response.sendRedirect("/showFavorites");
    }
}
