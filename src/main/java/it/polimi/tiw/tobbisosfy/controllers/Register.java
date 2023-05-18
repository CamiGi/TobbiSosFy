package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.DAOs.UserDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/Register")
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public Register() {
        super();
    }
    @Override
    public void init() throws ServletException {
        try {
            connection = DBServletInitializer.init(getServletContext());
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usrn = request.getParameter("nickname");
        String pwd = request.getParameter("password");
        UserDAO creator = new UserDAO(connection);
        final WebContext ctx = DBServletInitializer.createContext(request, response, getServletContext());
        String path = getServletContext().getContextPath();

        if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty()) {
            registrationFailed(response, ctx, "Missing parameters");
            return;
        }

        //does all the controls on the password
        if (pwd.length() < 8) {
            registrationFailed(response, ctx, "Password is too short");
            return;
        }
        if (pwd.length() > 20) {
            registrationFailed(response, ctx, "Password is too long");
            return;
        }
        if (pwd.toLowerCase().equals(pwd)) {
            registrationFailed(response, ctx, "Password has no uppercase chars");
            return;
        }
        if (pwd.toUpperCase().equals(pwd)) {
            registrationFailed(response, ctx, "Password has no lowercase chars");
            return;
        }
        if (!pwd.contains("0") &&
            !pwd.contains("1") &&
            !pwd.contains("2") &&
            !pwd.contains("3") &&
            !pwd.contains("4") &&
            !pwd.contains("5") &&
            !pwd.contains("6") &&
            !pwd.contains("7") &&
            !pwd.contains("8") &&
            !pwd.contains("9")) {
            registrationFailed(response, ctx, "Password has no numeric chars");
            return;
        }
        if (!pwd.contains("-") &&
            !pwd.contains("_") &&
            !pwd.contains("=") &&
            !pwd.contains("+") &&
            !pwd.contains("/") &&
            !pwd.contains("£") &&
            !pwd.contains("&") &&
            !pwd.contains("%") &&
            !pwd.contains("^") &&
            !pwd.contains("@") &&
            !pwd.contains("`") &&
            !pwd.contains("#") &&
            !pwd.contains(".") &&
            !pwd.contains(",") &&
            !pwd.contains("!") &&
            !pwd.contains("?") &&
            !pwd.contains(">") &&
            !pwd.contains("<") &&
            !pwd.contains(":")) {
            registrationFailed(response, ctx, "Password has no special chars");
            return;
        }
        if (!pwd.equals(request.getParameter("conf"))) {
            registrationFailed(response, ctx, "Password and confirmation are different");
            return;
        }

        try {
            creator.addUser(usrn, pwd);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry"))
                registrationFailed(response, ctx, "Username already taken");
            else
                registrationFailed(response, ctx, e.getMessage());
            return;
        }
        response.sendRedirect(path + "/UserRegisteredPage.html");
    }

    private void registrationFailed(HttpServletResponse response, WebContext ctx, String err) throws IOException {
        ctx.setVariable("error", err);
        templateEngine.process("/RegistrationPage.html", ctx, response.getWriter());
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
