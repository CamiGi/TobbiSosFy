package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.beans.User;

import javax.servlet.RequestDispatcher;
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
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usrn = request.getParameter("nickname");
        String pwd = request.getParameter("password");
        //UserDao creator;
        User u = null;
        String path = getServletContext().getContextPath();

        if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
            //da rifare con thymeleaf
        }

        /*
        //checks if the username exists
        if (userDAO.usernameAlreadyTaken(usern)) {
            response.sendError(HttpServletRespone.SC_BAD_REQUEST, "This username already exists");
            return;
            //da rifare con thymeleaf
        }
        */
        //does all the controls on the password
        if (pwd.length() < 8) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "This password is too short");
            return;
            //da rifare con thymeleaf
        }
        if (pwd.length() > 20) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "This password is too long");
            return;
            //da rifare con thymeleaf
        }
        if (pwd.toLowerCase().equals(pwd)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your password must contain at least one uppercase character");
            return;
            //da rifare con thymeleaf
        }
        if (pwd.toUpperCase().equals(pwd)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your password must contain at least one lowercase character");
            return;
            //da rifare con thymeleaf
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your password must contain at least one numeric character");
            return;
            //da rifare con thymeleaf
        }
        if (!pwd.contains("-") &&
                !pwd.contains("_") &&
                !pwd.contains("=") &&
                !pwd.contains("+") &&
                !pwd.contains("/") &&
                !pwd.contains("£") &&
                !pwd.contains("%") &&
                !pwd.contains("^") &&
                !pwd.contains("@") &&
                !pwd.contains("`") &&
                !pwd.contains("#")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your password must contain at least one special character");
            return;
            //da rifare con thymeleaf
        }

        //u = creator.createUser(usern, pwd);
        response.sendRedirect(path + "/UserRegisteredPage.html");
    }

    private void registrationFailed(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
        RequestDispatcher re = request.getRequestDispatcher(path + "/RegistrationPage.html");
        re.forward(request, response);
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
