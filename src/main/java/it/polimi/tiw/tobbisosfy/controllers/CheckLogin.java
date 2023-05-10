package it.polimi.tiw.tobbisosfy.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.polimi.tiw.tobbisosfy.beans.User;
import it.polimi.tiw.tobbisosfy.DAOs.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public CheckLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usrn = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        String path = getServletContext().getContextPath();
        final WebContext ctx = DBServletInitializer.createContext(request, response, getServletContext());

        if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty()) {
            ctx.setVariable("error", "Missing parameters");
            templateEngine.process("/index.html", ctx, response.getWriter());
            return;
        }

        UserDAO usr = new UserDAO(connection);
        User u;
        try {
            u = usr.login(usrn, pwd);
        } catch (Exception e) {
            ctx.setVariable("error", e.getMessage());
            templateEngine.process("/index.html", ctx, response.getWriter());
            return;
        }
        if (u == null) {
            ctx.setVariable("error", "An error occurred while creating user");
            templateEngine.process("/index.html", ctx, response.getWriter());
            return;
        } else {
            request.getSession().setAttribute("user", u);
        }
        //response.sendRedirect(path+"/Home");
        response.sendRedirect(path+"/ShowPlaylist?playlist=1&group=0");
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
