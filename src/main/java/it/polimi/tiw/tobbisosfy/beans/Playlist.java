package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.sql.Date;

@JavaBean
public class Playlist {
    private String title;
    private Date date;
    private User user;

    public Playlist(String title, java.sql.Date date, User user) {
        this.title =title.toLowerCase();
        this.date=date;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public java.sql.Date getDate() {
        return date;
    }
}
