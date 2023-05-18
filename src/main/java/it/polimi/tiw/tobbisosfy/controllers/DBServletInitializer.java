package it.polimi.tiw.tobbisosfy.controllers;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBServletInitializer {

    static Connection init(ServletContext context) throws SQLException, ClassNotFoundException {
        try {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static WebContext createContext (HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        return new WebContext(request, response, servletContext, request.getLocale());
    }
}
