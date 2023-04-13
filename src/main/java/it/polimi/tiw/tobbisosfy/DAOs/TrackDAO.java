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

    public void createTrack(Track track, Album album, Artist artist, User user) throws SQLException, Exception {
        String queryArID="SELECT * FROM artist WHERE artistID= ?";
        String queryAlID="SELECT * FROM album WHERE albumID= ?";
        String queryTID="SELECT * FROM track WHERE trackID= ?";
        String queryNewTrack = "INSERT INTO track VALUES (?, ?, ?, ?)";
        String queryNewAlbum = "INSERT INTO album VALUES (?, ?, ?, ?, ?)";
        String queryNewArtist = "INSERT INTO artist VALUES (?, ?, ?, ?)";
        ResultSet result= null;
        PreparedStatement pstatement = null;
        int rescode = 0;
        pstatement = con.prepareStatement(queryArID);
        pstatement.setLong(1,artist.getId());
        result = pstatement.executeQuery();
        if(!result.first()){
            rescode = newArtist(pstatement, rescode, artist, user, queryNewArtist);
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene");
            }
            rescode = newAlbum(pstatement, rescode, album, user, queryNewAlbum);
            if(rescode != 1){
                throw new Exception("ATTENZIONE quaclosa non è andato bene");
            }
            rescode = newTrack(pstatement, rescode, track, user, queryNewTrack);
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene");
            }
        } else {
            pstatement = con.prepareStatement(queryAlID);
            pstatement.setLong(1, album.getId());
            result = pstatement.executeQuery();
        }
    }

    private int newArtist(PreparedStatement ps, int code, Artist artist, User user, String queryNewArtist) throws SQLException{
        try{
            ps = con.prepareStatement(queryNewArtist);
            ps.setLong(1, artist.getId());
            ps.setString(2, artist.getName());
            ps.setString(3, artist.getGenre().toString());
            ArrayList a = new ArrayList<String>();
            artist.getAlbums().stream().forEach(s->{a.add(s.getName());});  //parkour
            ps.setArray(4,(Array)a);
            code= ps.executeUpdate();  //returns the number of rows inserted into the table
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();  //perchè?
                }
            } catch (Exception e1) {
                System.out.println("Altra eccezione: "+e1.getMessage());
            }
        }
        return code;
    }

    private int newAlbum(PreparedStatement ps, int code, Album album, User user, String queryNewAlbum) throws SQLException{
        try{
            ps = con.prepareStatement(queryNewAlbum);
            ps.setLong(1, album.getId());
            ps.setString(2, album.getName());
            ArrayList a = new ArrayList<String>();
            album.getTracks().stream().forEach(s->{a.add(s.getTitle());});
            ps.setArray(3,(Array)a);
            ps.setString(4, album.getArtist().getName());
            ps.setString(5, album.getImgUri());
            code = ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();  //perchè?
                }
            } catch (Exception e1) {
                System.out.println("Altra eccezione: "+e1.getMessage());
            }
        }
        return code;
    }

    private int newTrack(PreparedStatement ps, int code, Track track, User user, String queryNewTrack) throws SQLException{
        try{
            ps = con.prepareStatement(queryNewTrack);
            ps.setLong(1, track.getId());
            ps.setString(2, track.getTitle());
            ps.setString(3,track.getMp3Uri());
            ps.setLong(4, track.getUser().getId());
            code = ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();  //perchè?
                }
            } catch (Exception e1) {
                System.out.println("Altra eccezione: "+e1.getMessage());
            }
        }
        return code;
    }
}
