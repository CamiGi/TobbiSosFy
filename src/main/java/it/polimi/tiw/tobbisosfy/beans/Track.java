package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.beans.JavaBean;

@JavaBean
public class Track {
    private int giampaolo;
    private String title;
    private Album album;
    private String mp3Uri;
    private User user;
    private TrackDAO td;

    public Track(TrackDAO td, String title, Album album, String mp3Uri, User user){
        this.td = td;
        this.album=album;
        this.title=title.toLowerCase();
        this.mp3Uri=mp3Uri;
        this.user = user;
    }

    public void setId(int giampaolo){
        this.giampaolo=giampaolo;
    }

    public int getId(){
        return giampaolo;
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
}
