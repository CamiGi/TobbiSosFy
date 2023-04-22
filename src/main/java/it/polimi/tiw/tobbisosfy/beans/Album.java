package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;


public class Album {
    private String name;
    private int year;
    private Genre genre;
    private Artist artist;
    private String imgUri;
    private TrackDAO td;

    public Album(TrackDAO td, String name, int year, Genre genre, Artist artist, String imgUri) {
        this.td = td;
        this.name = name.toLowerCase();
        this.year = year;
        this.genre = genre;
        this.artist = artist;
        this.imgUri = imgUri;
    }

    public String getName() {
        return name;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getImgUri() {
        return imgUri;
    }

    public int getYear() {
        return year;
    }

    public Genre getGenre() {
        return genre;
    }
}
