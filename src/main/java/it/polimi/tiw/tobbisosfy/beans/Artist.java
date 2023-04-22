package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.beans.JavaBean;

@JavaBean
public class Artist {

    private String name;
    private TrackDAO td;

    public Artist(String name, TrackDAO td){
        this.td = td;
        this.name=name.toLowerCase();
    }
    public String getName() {
        return name;
    }
}
