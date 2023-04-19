package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.time.LocalDateTime;

@JavaBean
public class Playlist {
    private final Long id;  //vorrei fosse il db ad assegnarglielo da solo
    private String name;
    private LocalDateTime date;
    private User user;

    public Playlist(Long id, String name, LocalDateTime date, User user) {
        this.id = id;  //vorrei fosse il db ad assegnarglielo da solo
        this.name = name.toLowerCase();
        this.date=date;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void getDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }
}
