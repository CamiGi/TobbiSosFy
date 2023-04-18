package it.polimi.tiw.tobbisosfy.controllers;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBServletInitializer {

    static Connection init(ServletContext context) throws SQLException, ClassNotFoundException {
        String driver = context.getInitParameter("dbDriver");
        String url = context.getInitParameter("dbUrl");
        String user = context.getInitParameter("dbUser");
        String password = context.getInitParameter("dbPassword");
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

}
