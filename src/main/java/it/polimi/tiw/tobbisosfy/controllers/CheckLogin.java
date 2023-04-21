package it.polimi.tiw.tobbisosfy.controllers;

import java.io.IOException;
import java.sql.DriverManager;
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

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

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

        if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        //UserDAO usr = new UserDAO(connection);
        User u = null;
        /*try {
            u = usr.checkCredentials(usrn, pwd);
        } catch (SQLException e) {
            // throw new ServletException(e);
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
            return;
        }*/
        String path = getServletContext().getContextPath();
        if (u == null) {
            path = getServletContext().getContextPath() + "/index.html";
        } else {
            request.getSession().setAttribute("user", u);
        }
        response.sendRedirect(path);
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}