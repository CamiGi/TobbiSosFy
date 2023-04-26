package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.User;

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
     * @return l'oggetto user
     * @throws SQLException
     * @throws Exception se lo username o la password sono errate
     */
    public User login(String username, String password) throws SQLException, Exception{
        ps = con.prepareStatement(queryUsername);
        ps.setString(1,username);
        result = ps.executeQuery();

        if (result.isBeforeFirst()){
            ps = con.prepareStatement(queryLogin);
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeQuery();
            if (!result.isBeforeFirst()){
                throw new Exception("ATTENZIONE password errata");
            }
        } else {
            throw new Exception("ATTENZIONE username errato");
        }
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
        int code = 0;

        ps = con.prepareStatement(queryNewUser);
        ps.setString(1, username);
        ps.setString(2, password);
        code = ps.executeUpdate();

        if (!(code == 1)){
            con.rollback();
            throw new Exception("ATTENZIONE qualcosa è andato storto: 600");
        }
    }

}
