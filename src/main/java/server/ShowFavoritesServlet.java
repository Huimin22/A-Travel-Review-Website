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

public class ShowFavoritesServlet extends HttpServlet {

    /**
     * Retrieves and displays the favorite hotels for the logged-in user, or redirects to login if not authenticated.
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
        List<String> favoriteHotels = dbHandler.getFavoriteHotels(username);
        context.put("favoriteHotels", favoriteHotels);
        Template template = ve.getTemplate("templates/displayFavoriteHotels.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

}
