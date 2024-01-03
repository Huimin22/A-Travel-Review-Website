package server;

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
import java.util.List;

public class RegisterServlet extends HttpServlet {

    /**
     * Handles GET requests for the registration page.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If there is an I/O error.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        // Get the parameter from the GET request
        String usernameTaken = request.getParameter("usernameTaken");
        usernameTaken = StringEscapeUtils.escapeHtml4(usernameTaken);
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/register.html");
        context.put("servlet", request.getServletPath());

        if (usernameTaken != null && !usernameTaken.isEmpty()) {
            context.put("usernameTaken", true);
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        if (session.getAttribute("username") != null) {
            response.sendRedirect("/home");
        }
        else {
            out.println(writer);
        }

    }

    /**
     * Handles POST requests for user registration.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws IOException If there is an I/O error.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        // Get the parameters from the GET request
        String username = request.getParameter("name");
        username = StringEscapeUtils.escapeHtml4(username);
        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml4(password);
        System.out.println(username);
        System.out.println(password);

        // Validate if the username is taken
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        List<String> existingUsernames = databaseHandler.getAllUsers();

        if (existingUsernames.contains(username)) {
            response.sendRedirect("/register?usernameTaken=true");
            return;
        }

        // Validate username and password
        if (isValidUsername(username) && isValidPassword(password)) {
            databaseHandler.registerUser(username, password);
            response.sendRedirect("/login?register=true");
        } else {
            response.sendRedirect("/register?success=false");
        }
    }

    /**
     * Validates if the provided username is in the correct format.
     *
     * @param username The username to validate.
     * @return True if the username is valid, false otherwise.
     */
    private boolean isValidUsername(String username) {
        String regExp = "[A-Za-z][A-Za-z\\d_-]{4,20}";
        if (username.matches(regExp)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * Validates if the provided password is in the correct format.
     *
     * @param password The password to validate.
     * @return True if the password is valid, false otherwise.
     */
    private boolean isValidPassword(String password) {
        String regExp = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[#?!@$%^&*_-])[A-Za-z\\d#?!@$%^&*_-].{6,}$";
        if (password.matches(regExp)) {
            return true;
        }else {
            return false;
        }
    }





}
