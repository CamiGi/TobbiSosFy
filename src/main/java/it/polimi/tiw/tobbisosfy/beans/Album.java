package it.polimi.tiw.tobbisosfy.beans;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;

import java.util.ArrayList;


public class Album {
    private final Long id; //vorrei fosse il db ad assegnarglielo da solo
    private String name;
    private Artist artist;
    private ArrayList<Track> tracks;
    private String imgUri;
    private TrackDAO td;


    public Album (TrackDAO td, String name, Track track, Artist artist, String imgUri){
        this.td = td;
        this.id = this.getIdAssing();  //chiamo metodo privato
        this.name = name.toLowerCase();
        this.artist = artist;
        this.imgUri = imgUri;
        this.tracks.add(track);
    }

    private Long getIdAssing(){
        Long a = Long.valueOf(0);
        boolean f = false;
        try {
            a = (td.getLastIdAlbum() + 1);  //chiama metodo di TrackDAO
            f = true;
        } catch (Exception e) {
            if (e.equals("Non ci sono righe")){
                f = false;
            }
        }
        if (f){
            return a;  //ritorno il valore da assengare in caso ci siano già delle canzoni dentro
        } else {
            return Long.valueOf(0);  //ritorno 0 perchè il primo id in caso di db vuoto
        }
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
