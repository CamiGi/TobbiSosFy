package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;


public class Album {
    private String title;
    private int year = 0;
    private Genre genre;
    private Artist artist;
    private String imgUri;
    private TrackDAO td;

    public Album(TrackDAO td, String name, int year, Genre genre, Artist artist, String imgUri) {
        this.td = td;
        this.title = name.toLowerCase();
        this.year = year;
        this.genre = genre;
        this.artist = artist;
        this.imgUri = imgUri;
    }

    public String getTitle() {
        return title;
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

    public boolean isEmpty(){
        return (this.title.isEmpty() || this.year == 0 ||
                this.imgUri.isEmpty() || this.artist.isEmpty());
    }
    @Override
    public String toString(){
        return "Album={title="+this.title+", year="+this.year+", genre="+this.genre+", artist="+this.artist.toString()+", imgUri="+this.imgUri+"}";
    }
}
