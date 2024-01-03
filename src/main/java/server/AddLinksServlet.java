package server;

import org.apache.commons.text.StringEscapeUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AddLinksServlet extends HttpServlet {

    /**
     * Handles a GET request to store a link for a logged-in user, redirecting to login page if the user is not authenticated.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);
        String link = request.getParameter("link");
        link = StringEscapeUtils.escapeHtml4(link);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
//        dbHandler.createHistoryTable();
        dbHandler.storeLink(username, link);

    }


}
