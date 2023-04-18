package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;

@JavaBean
public class Track {
    private final Long id;  //vorrei fosse il db ad assegnarglielo da solo
    private String title;
    private String mp3Uri;
    private User user;

    //io inserirei anche l'album, da li si prendono artista, anno e immagine

    public Track(Long id, String title, String mp3Uri, User user){
            this.id=id;  //vorrei fosse il db ad assegnarglielo da solo
            this.title=title;
            this.mp3Uri=mp3Uri;
            this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public String getMp3Uri() {
        return mp3Uri;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
}
