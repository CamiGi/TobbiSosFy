package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;

@JavaBean
public class Artist {

    private final String artistName;

    public Artist(String name){
        this.artistName=name;
    }
    public String getArtistName() {
        return artistName;
    }

    public boolean isEmpty() {
        return artistName.isEmpty();
    }

    @Override
    public String toString(){
        return "Artist={name="+this.artistName+"}";
    }
}
