package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.beans.JavaBean;

@JavaBean
public class Artist {

    private String artistName;
    private TrackDAO td;

    public Artist(String name, TrackDAO td){
        this.td = td;
        this.artistName=name.toLowerCase();
    }
    public String getArtistName() {
        return artistName;
    }

    public boolean isEmpty() {
        return artistName.isEmpty();
    }
}
