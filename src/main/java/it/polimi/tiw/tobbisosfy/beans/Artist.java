package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.util.ArrayList;

@JavaBean
public class Artist {

    private final Long id; //vorrei fosse il db ad assegnarglielo da solo
    private String name;
    private Genre genre;
    private ArrayList<Album> albums;

    public Artist(Long id, String name, Genre genre, ArrayList<Album> albums){
        this.id=id; //vorrei fosse il db ad assegnarglielo da solo
        this.name=name;
        this.genre=genre;
        this.albums = albums;
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
