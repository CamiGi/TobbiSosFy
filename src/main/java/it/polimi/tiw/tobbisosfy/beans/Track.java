package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.beans.JavaBean;

@JavaBean
public class Track {
    private int id;
    private final String title;
    private final Album album;
    private final String mp3Uri;
    private final User user;
    private TrackDAO td;

    public Track(TrackDAO td, String title, Album album, String mp3Uri, User user){
        this.td = td;
        this.album=album;
        this.title=title.toLowerCase();
        this.mp3Uri=mp3Uri;
        this.user = user;
    }

    public void setId(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMp3Uri() {
        return mp3Uri;
    }

    public User getUser() {
        return user;
    }

    public Album getAlbum() {
        return album;
    }

    @Override
    public String toString(){
        return "Track={title="+this.title+", album="+this.getAlbum().toString()+", uri="+this.mp3Uri+", user="+this.user.toString()+"}";
    }
}
