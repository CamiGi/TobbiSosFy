package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TrackDAO {
    private Connection con;

    public TrackDAO(Connection con){
        this.con=con;
    }

    public void createTrack(Track track, Album album, Artist artist, User user) throws SQLException {
        String queryArID="SELECT * FROM artist WHERE artistID= ?";
        String queryAlID="SELECT * FROM album WHERE artistID= ?";
        String queryTID="SELECT * FROM track WHERE artistID= ?";
        String queryNewTrack = "INSERT INTO track VALUES (?, ?, ?, ?)";
        String queryNewAlbum = "INSERT INTO album VALUES (?, ?, ?, ?, ?)";
        String queryNewArtist = "INSERT INTO artist VALUES (?, ?, ?, ?)";
        ResultSet result= null;
        PreparedStatement pstatement = null;
        try{
            pstatement = con.prepareStatement(queryArID);
            pstatement.setLong(1,artist.getId());
            result = pstatement.executeQuery();
            if(result.equals(null)){

            }
        }
    }

    private void newArtist(PreparedStatement ps, ResultSet rs, Artist artist, User user, String queryNewArtist) throws SQLException{
        try{
            ps = con.prepareStatement(queryNewArtist);
            ps.setLong(1, artist.getId());
            ps.setString(2, artist.getName());
            ps.setString(3, artist.getGenre().toString());
            ArrayList a = new ArrayList<String>();
            artist.getAlbums().stream().forEach(s->{a.add(s.getName());});
            ps.setArray(4,(Array)a);
            rs= ps.executeQuery();
        }
    }

    private void newAlbum(PreparedStatement ps, ResultSet rs, Album album, User user, String queryNewAlbum) throws SQLException{
        try{
            ps = con.prepareStatement(queryNewAlbum);
            ps.setLong(1, album.getId());
            ps.setString(2, album.getName());
            ArrayList a = new ArrayList<String>();
            album.getTracks().stream().forEach(s->{a.add(s.getTitle());});
            ps.setArray(3,(Array)a);
            ps.setString(4, album.getArtist().getName());
            ps.setString(5, album.getImgUri());
            rs= ps.executeQuery();
        }
    }
}
