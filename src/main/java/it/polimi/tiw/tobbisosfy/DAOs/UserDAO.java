package it.polimi.tiw.tobbisosfy.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;

    private String queryLogin = "SELECT * FROM user WHERE username=? AND password=?";
    private String queryUsername = "SELECT * FROM user WHERE username=?";
    private String queryPassword = "SELECT * FROM user WHERE password=?";

    private String queryNewUser = "INSERT INTO user VALUES (?, ?)";  //username, password

    public UserDAO(Connection con){
        this.con=con;
    }

    /**
     * Valutazione username e password corrette
     * @param username
     * @param password
     * @return true se ok, false se non va bene
     * @throws SQLException
     * @throws Exception
     */
    public boolean login(String username, String password) throws SQLException, Exception{
        ps = con.prepareStatement(queryUsername);
        ps.setString(1,username);
        result = ps.executeQuery();

        if (result.first()){
            ps = con.prepareStatement(queryLogin);
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeQuery();
            if (result.first()){
                return result.first();
            } else {
                throw new Exception("ATTENZIONE password errata");
            }
        } else {
            throw new Exception("ATTENZIONE username errato");
        }
    }

    /**
     * Aggiungo un nuovo utente
     * @param username
     * @param password
     * @param code
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public int addUser(String username, String password, int code) throws SQLException, Exception{
        code = 0;
        ps = con.prepareStatement(queryUsername);
        ps.setString(1,username);
        result = ps.executeQuery();

        if (!result.first()){
            ps = con.prepareStatement(queryLogin);
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeQuery();
            if (!result.first()){
                ps = con.prepareStatement(queryNewUser);
                ps.setString(1, username);
                ps.setString(2, password);
                code = ps.executeUpdate();
            } else {
                throw new Exception("ATTENZIONE password già esistente");
            }
        } else {
            throw new Exception("ATTENZIONE username già esistente");
        }
        return code;
    }

}
