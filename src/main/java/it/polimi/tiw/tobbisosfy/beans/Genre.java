package it.polimi.tiw.tobbisosfy.beans;


import java.beans.JavaBean;

@JavaBean
public enum Genre {
    CLASSIC("CLASSIC"), JAZZ("JAZZ"), BLUES("BLUES"), GOSPEL("GOSPEL"),
    SOUL("SOUL"), POP("POP"), ROCK("ROCK"), COUNTRY("COUNTRY"), DISCO("DISCO"),
    TECHNO("TECHNO"), RAGGAE("RAGGAE"), SALSA("SALSA"), FLAMENCO("FLAMENCO"), HIPHOP("HIPHOP"),
    METAL("METAL"), FUNK("FUNK");

    private final String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    public boolean isEmpty(){
        return genre.isEmpty();                                             
    }
}
