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

public class HomeServlet extends HttpServlet {
    /**
     * Handles HTTP GET requests for displaying the home page. This method is responsible
     * for processing requests, setting the content type and HTTP status, and rendering
     * the home page using a Velocity template.
     *
     * @param request  the HttpServletRequest object containing the request parameters
     * @param response the HttpServletResponse object for sending the response
     * @throws IOException if an I/O exception occurs while processing the request or response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
//        String lastLoginTime = request.getParameter("lastLoginTime");
//        lastLoginTime = StringEscapeUtils.escapeHtml4(lastLoginTime);
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/home.html");
        context.put("servlet", request.getServletPath());
        HttpSession session = request.getSession();
        String username = (String)session.getAttribute("username");
        context.put("username", username);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        boolean timeExist = dbHandler.checkTimeExist(username);
        String lastLoginTime = "";
        if (timeExist) {
            lastLoginTime = dbHandler.getLastLoginTime(username);
            if (lastLoginTime == null)
                lastLoginTime = "";
        }

        context.put("lastLoginTime", lastLoginTime);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        PrintWriter out = response.getWriter();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
        }
        else {
            out.println(writer);
        }
    }

}
