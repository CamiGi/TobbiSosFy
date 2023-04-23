package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.util.ArrayList;

@JavaBean
public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username.toLowerCase();
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
