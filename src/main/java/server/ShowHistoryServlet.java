package server;

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


public class ShowHistoryServlet extends HttpServlet {

    /**
     * Handles GET request to display the history of links for the logged-in user, redirecting to login page if not authenticated.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("/login");
            return;
        }

        PrintWriter out = response.getWriter();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        List<String> linkList = dbHandler.getLinkHistory(username);
        context.put("linkList", linkList);
        Template template = ve.getTemplate("templates/showHistory.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }


}
