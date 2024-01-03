package server;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LoginServlet extends HttpServlet {

//    public static String getCurrentDate() {
//        LocalDate currentDate = LocalDate.now();
//        return currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//    }
//
//    /**
//     * Returns the current time in HH:mm format.
//     */
//    public static String getCurrentTime() {
//        LocalTime currentTime = LocalTime.now();
//        return currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        // Get the parameter from the GET request
        String invalid = request.getParameter("error");
        invalid = StringEscapeUtils.escapeHtml4(invalid);
        String registerSuccess = request.getParameter("registerSuccess");
        registerSuccess = StringEscapeUtils.escapeHtml4(registerSuccess);

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/login.html");
        context.put("servlet", request.getServletPath());

        if (invalid != null && !invalid.isEmpty()) {
            context.put("error", true);
        }
        if (registerSuccess != null && !registerSuccess.isEmpty()) {
            context.put("registerSuccess", true);
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        String username = (String)session.getAttribute("username");
        if (username != null) {
            response.sendRedirect("/home");
        }
        else {
            out.println(writer);
        }
    }

    /**
     * Handles HTTP POST requests for processing user login.
     * @param request  the HttpServletRequest object containing the form data
     * @param response the HttpServletResponse object for sending the response
     * @throws IOException if an I/O exception occurs while processing the request or response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("name");
        username = StringEscapeUtils.escapeHtml4(username);
        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml4(password);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        boolean authenticated = dbHandler.authenticateUser(username, password);
        if (authenticated) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            boolean timeExist = dbHandler.checkTimeExist(username);
//            String lastLoginTime = "";
//            if (timeExist) {
//                lastLoginTime = dbHandler.getLastLoginTime(username);
//                dbHandler.updateLastLoginTable(username);
//            }
            if (timeExist) {
                dbHandler.UpdateCurrentTime(username);
            } else {
                dbHandler.AddCurrentTime(username);
            }

            response.sendRedirect("/home");
        } else {
            response.sendRedirect("/login?error=true");
        }
    }

}
