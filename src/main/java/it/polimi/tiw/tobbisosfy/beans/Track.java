package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.beans.JavaBean;

@JavaBean
public class Track {
    private final Long id;  //vorrei fosse il db ad assegnarglielo da solo
    private String title;
    private String mp3Uri;
    private User user;
    private TrackDAO td;

    //io inserirei anche l'album, da li si prendono artista, anno e immagine

    public Track(TrackDAO td, String title, String mp3Uri, User user){
        this.td = td;
        this.id = this.getIdAssing();  //chiamo metodo privato
        this.title=title.toLowerCase();
        this.mp3Uri=mp3Uri;
        this.user = user;
    }

    private Long getIdAssing(){
        Long a = Long.valueOf(0);
        boolean f = false;
        try {
            a = (td.getLastIdTrack() + 1);  //chiama metodo di TrackDAO
            f = true;
        } catch (Exception e) {
            if (e.equals("Non ci sono righe")){
                f = false;
            }
        }
        if (f){
            return a;  //ritorno il valore da assengare in caso ci siano già delle canzoni dentro
        } else {
            return Long.valueOf(0);  //ritorno 0 perchè il primo id in caso di db vuoto
        }
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
