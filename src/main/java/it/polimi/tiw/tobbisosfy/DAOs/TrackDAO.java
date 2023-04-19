package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class TrackDAO {
    private Connection con;
    private ResultSet result= null;
    private PreparedStatement pstatement = null;
    private Statement statement = null;

    public TrackDAO(Connection con){
        this.con=con;
    }

    public void createTrack(Track track, Album album, Artist artist, User user) throws SQLException, Exception {
        String queryArID="SELECT ID, name FROM artist WHERE name= ?";
        String queryAlID="SELECT ID, name FROM album WHERE name= ?";
        String queryTID="SELECT ID, title FROM track WHERE title= ?";
        String queryNewTrack = "INSERT INTO track VALUES (?, ?, ?, ?)";
        String queryNewAlbum = "INSERT INTO album VALUES (?, ?, ?, ?, ?)";
        String queryNewArtist = "INSERT INTO artist VALUES (?, ?, ?, ?)";
        int rescode = 0;
        pstatement = con.prepareStatement(queryArID);   //vedo se ho l'artista
        pstatement.setString(1,artist.getName());
        result = pstatement.executeQuery();  //result set con una riga sola se l'artista esiste, se non esiste non ho nessuna riga
        if(!result.first()){  //se non ho nessun artista
            rescode = newArtist(pstatement, rescode, artist, user, queryNewArtist);  //creo artista
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 100");
            }
            rescode = newAlbum(pstatement, rescode, album, user, queryNewAlbum);  //creo album
            if(rescode != 1){
                throw new Exception("ATTENZIONE quaclosa non è andato bene : 101");
            }
            rescode = newTrack(pstatement, rescode, track, user, queryNewTrack);  //creo track
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 102");
            }
        } else {  //se ho già l'artista
            pstatement = con.prepareStatement(queryAlID);  //vedo se esite l'album
            pstatement.setString(1, album.getName());
            result = pstatement.executeQuery();  //mando la query
            if(!result.first()){  //se non esiste l'album
                rescode = newAlbum(pstatement, rescode, album, user, queryNewAlbum);  //creo album
                if(rescode != 1){
                    throw new Exception("ATTENZIONE quaclosa non è andato bene : 200");
                }
                rescode = newTrack(pstatement, rescode, track, user, queryNewTrack);  //creo track
                if(rescode != 1){
                    throw new Exception("ATTENZIONE qualcosa non è andato bene : 201");
                }
            } else {  //caso in cui esiste l'artista e l'album
                pstatement = con.prepareStatement(queryTID);  //vedo se esiste la track
                pstatement.setString(1,track.getTitle());
                result = pstatement.executeQuery();
                if(!result.first()){  // la track non esiste
                    rescode = newTrack(pstatement, rescode, track, user, queryNewTrack);  //creo track
                    if(rescode != 1){
                        throw new Exception("ATTENZIONE qualcosa non è andato bene : 300");
                    }
                } else {
                    throw new Exception("ATTENZIONE LA CANZONE è GIà PRESENTE NEL DATABASE");  //la canzone è già presente
                }
            }
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

    public Long getLastIdArtist() throws SQLException, Exception{
        String query="SELECT ID FROM artist";  //creo la query
        return getLId(query);
    }

    public Long getLastIdAlbum() throws SQLException, Exception{
        String query="SELECT ID FROM album";  //creo la query
        return getLId(query);
    }

    public Long getLastIdTrack() throws SQLException, Exception{
        String query="SELECT ID FROM track";  //creo la query
        return getLId(query);
    }


    private Long getLId(String query) throws  SQLException, Exception{
        boolean w;
        String q = query;
        pstatement = con.prepareStatement(query);
        result = pstatement.executeQuery();  //ottengo tutti gli ID degli artisti
        w = result.last();  //muovo il cursore nell'ultima riga
        if(w == false){
            throw new Exception("Non ci sono righe");
        }
        return (Long) result.getObject("ID");  //ritorno l'id
    }
}
