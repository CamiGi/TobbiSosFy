package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.sql.Date;

@JavaBean
public class Playlist {
    private int id;
    private final String title;
    private final Date date;
    private final User user;

    public Playlist(String title, java.sql.Date date, User user) {
        this.title =title.toLowerCase();
        this.date=date;
        this.user = user;
    }

    public void setId(int id){
        this.id = id;
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

    @Override
    public String toString(){
        return "Playlist={title="+this.title+", date="+this.date.toString()+", user="+this.user.toString()+"}";
    }
}
