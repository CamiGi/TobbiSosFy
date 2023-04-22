package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.time.LocalDateTime;
import java.util.ArrayList;

@JavaBean
public class Playlist {
    private String title;
    private LocalDateTime date;
    private User user;

    public Playlist(Long id, String title, LocalDateTime date, User user) {
        this.title =title.toLowerCase();
        this.date=date;
        this.user = user;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
