package it.polimi.tiw.tobbisosfy.beans;

import java.util.ArrayList;


public class Album {
    private final Long id; //vorrei fosse il db ad assegnarglielo da solo
    private String name;
    private Artist artist;
    private ArrayList<Track> tracks;
    private String imgUri;

    public Album (Long id, String name, Track track, Artist artist, String imgUri){
        this.id=id; //vorrei fosse il db ad assegnarglielo da solo
        this.name=name;
        this.artist = artist;
        this.imgUri=imgUri;
        this.tracks.add(track);
    }

    public String getName() {
        return name;
    }

    public Artist getArtist() {
        return artist;
    }

    public ArrayList<Track> getTracks() {  ////////////////////////////////
        return (ArrayList<Track>) tracks.clone();
    }

    public String getImgUri() {
        return imgUri;
    }

    public Long getId() {
        return id;
    }
}
