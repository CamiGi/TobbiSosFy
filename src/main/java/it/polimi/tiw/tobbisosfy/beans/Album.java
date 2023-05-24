package it.polimi.tiw.tobbisosfy.beans;

public class Album {
    private final String title;
    private final int year;
    private final Genre genre;
    private final Artist artist;
    private final String imgUri;

    public Album(String name, int year, Genre genre, Artist artist, String imgUri) {
        this.title = name;
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
