package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.sql.Date;

@JavaBean
public class Playlist {
    private final int id;
    private String title;
    private Date date;
    private User user;

    public Playlist(int id, String title, java.sql.Date date, User user) {
        this.id = id;
        this.title =title.toLowerCase();
        this.date=date;
        this.user = user;
    }

    public int getId() {
        return id;
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
