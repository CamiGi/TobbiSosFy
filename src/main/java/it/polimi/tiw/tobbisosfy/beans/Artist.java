package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.beans.JavaBean;
import java.util.ArrayList;

@JavaBean
public class Artist {

    private final Long id; //vorrei fosse il db ad assegnarglielo da solo
    private String name;
    private Genre genre;
    private ArrayList<Album> albums;
    private TrackDAO td;

    public Artist(Long id, String name, Genre genre, ArrayList<Album> albums){
        this.td = td;
        this.id = this.getIdAssing();  //chiamo metodo privato
        this.name=name.toLowerCase();
        this.genre=genre;
        this.albums = albums;
    }

    private Long getIdAssing(){
        Long a = Long.valueOf(0);
        boolean f = false;
        try {
            a = (td.getLastIdArtist() + 1);  //chiama metodo di TrackDAO
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

    public String getName() {
        return name;
    }

    public Genre getGenre() {
        return genre;
    }

    public Long getId() {
        return id;
    }

    public ArrayList<Album> getAlbums() { ///////////
        return albums;
    }
}
