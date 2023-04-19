package it.polimi.tiw.tobbisosfy.beans;

import java.beans.JavaBean;
import java.util.ArrayList;

@JavaBean
public class User {
    private final Long id;
    private String username;
    private String password;
    private ArrayList<Playlist> playlists;
    private ArrayList<Track> tracks;

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username.toLowerCase();
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
