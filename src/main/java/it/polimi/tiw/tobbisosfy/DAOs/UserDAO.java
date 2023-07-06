package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection con;
    private PreparedStatement ps;

    public UserDAO(Connection con){
        this.con=con;
    }

    /**
     * Valutazione username e password corrette
     * @param username
     * @param password
     * @return l'oggetto user
     * @throws SQLException
     * @throws Exception se lo username o la password sono errate
     */
    public User login(String username, String password) throws SQLException, Exception{
        String queryUsername = "SELECT * FROM user WHERE username=?";
        ps = con.prepareStatement(queryUsername);
        ps.setString(1,username);
        ResultSet result = ps.executeQuery();

        if (result.isBeforeFirst()){
            String queryLogin = "SELECT * FROM user WHERE username=? AND password=?";
            ps = con.prepareStatement(queryLogin);
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeQuery();
            if (!result.isBeforeFirst()){
                throw new Exception("Wrong password");
            }
        } else {
            throw new Exception("Username not found");
        }
        result.next();
        return new User(result.getString("username"), result.getString("password"));
    }

    /**
     * Aggiungo un nuovo utente
     * @param username
     * @param password
     * @throws SQLException
     * @throws Exception ATTENZIONE username già esistente
     */
    public void addUser(String username, String password) throws SQLException, Exception{
        int code;

        String queryNewUser = "INSERT INTO user VALUES (?, ?)";
        ps = con.prepareStatement(queryNewUser);
        ps.setString(1, username);
        ps.setString(2, password);
        code = ps.executeUpdate();

        if (code != 1){
            con.rollback();
            throw new Exception("ATTENTION something went wrong: 600");
        }
    }

}
