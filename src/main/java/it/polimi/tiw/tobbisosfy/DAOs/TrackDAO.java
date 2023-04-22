package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class TrackDAO {
    private Connection con;
    private ResultSet result= null;
    private PreparedStatement pstatement = null;
    private PreparedStatement ps;
    private String queryArID="SELECT ID, name FROM artist WHERE name= ?";
    private String queryAlID="SELECT ID, name FROM album WHERE name= ?";
    private String queryTID="SELECT ID, title FROM track WHERE title= ?";
    private String queryNewTrack = "INSERT INTO track VALUES (?, ?, ?, ?)"; //title, albumID, file, username
    private String queryNewAlbum = "INSERT INTO album VALUES (?, ?, ?, ?, ?)"; //name, year, genre, artistID, img
    private String queryNewArtist = "INSERT INTO artist VALUES (?)";  //name

    public TrackDAO(Connection con){
        this.con=con;
    }

    public void createTrack(Track track, User user) throws SQLException, Exception {
        int rescode = 0;
        pstatement = con.prepareStatement(queryArID);   //vedo se ho l'artista
        pstatement.setString(1,track.getAlbum().getArtist().getName());
        result = pstatement.executeQuery();  //result set con una riga sola se l'artista esiste, se non esiste non ho nessuna riga
        if(!result.first()){  //se non ho nessun artista
            rescode = newArtist(pstatement, rescode, track.getAlbum().getArtist(), user, queryNewArtist);  //creo artista: name
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 100");
            }
            rescode = newAlbum(pstatement, rescode, track.getAlbum(), user, queryNewAlbum);  //creo album: creo album: name, year, genre, artistID, img
            if(rescode != 1){
                throw new Exception("ATTENZIONE quaclosa non è andato bene : 101");
            }
            rescode = newTrack(pstatement, rescode, track, user, queryNewTrack);  //creo track: title, albumID, file, username
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 102");
            }
        } else {  //se ho già l'artista
            pstatement = con.prepareStatement(queryAlID);  //vedo se esite l'album
            pstatement.setString(1, track.getAlbum().getName());
            result = pstatement.executeQuery();  //mando la query
            if(!result.first()){  //se non esiste l'album
                rescode = newAlbum(pstatement, rescode, track.getAlbum(), user, queryNewAlbum);  //creo album: name, year, genre, artistID, img
                if(rescode != 1){
                    throw new Exception("ATTENZIONE quaclosa non è andato bene : 200");
                }
                rescode = newTrack(pstatement, rescode, track, user, queryNewTrack);  //creo track: title, albumID, file, username
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

    //creo artista: name
    private int newArtist(int code,  Artist artist) throws SQLException{
        try{
            ps = con.prepareStatement(queryNewArtist);
            ps.setString(1, artist.getName());
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

    //creo album: name, year, genre, artistID, img
    private int newAlbum(int code, Album album) throws SQLException{
        try{
            ps=con.prepareStatement("SELECT ID FROM artist WHERE name=?");  //chiedo al db l'id dell'artista col nome che compare nell'oggetto album
            ps.setString(1,album.getArtist().getName());
            ResultSet re =ps.executeQuery();
            ps = con.prepareStatement(queryNewAlbum);
            ps.setString(1, album.getName());
            ps.setInt(2, album.getYear());
            ps.setString(3, album.getGenre().toString());
            ps.setInt(4, re.getInt("ID"));
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

    //creo track: title, albumID, file, username
    private int newTrack(int code, Track track, User user) throws SQLException{
        try{
            ps=con.prepareStatement("SELECT ID FROM album WHERE name=?");  //chiedo al db l'id dell'album col nome che compare nell'oggetto track
            ps.setString(1,track.getAlbum().getName());
            ResultSet re =ps.executeQuery();
            ps=con.prepareStatement("SELECT username FROM user WHERE username=?");  //chiedo al db l'id dell'album col nome che compare nell'oggetto track
            ps.setString(1,user.getUsername());
            ResultSet re1 =ps.executeQuery();
            ps = con.prepareStatement(queryNewTrack);
            ps.setString(1, track.getTitle());
            ps.setInt(2, re.getInt("ID"));
            ps.setString(3,track.getMp3Uri());
            ps.setString(4, re1.getString("username"));  //ridondante ma almeno se c'è un errore nel db viene lanciato
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

    /**
     * DA RIFARE
     * @param playlist
     * @return
     * @throws SQLException
     */
    public ArrayList<Track> getTracksFromPlaylist(Playlist playlist) throws SQLException{
        ArrayList<Track> rs = new ArrayList<>();
        /*String queryTraks = "SELECT trackID FORM contains WHERE playlistID=?";  //creo query che seleziona i track id
        String queryPlayID = "SELECT ID FORM playlist WHERE name=?";  //creo query che trova l'id della playlist che mi interessa
        ps = con.prepareStatement(queryPlayID);  //settaggio prepared statement
        ps.setString(1,playlist.getTitle());
        result = ps.executeQuery();  //mando la query playlist
        ps = con.prepareStatement(queryTraks);  //settaggio altro statement
        ps.setInt(1,result.getInt("ID"));
        result = ps.executeQuery();  //mando la query per sapere gli id delle canzoni
        ArrayList<Integer> ids = new ArrayList<Integer>();
        result.first();
        ids.add(result.getInt("trackID"));  //inserisco gli id
        while (!result.isAfterLast()){
            result.next();
            ids.add(result.getInt("trackID"));
        }*/
        return  rs;
    }

    /*private Track getTrack(Playlist playlist, int trackId) throws SQLException {
        Track result_t;
        Album result_al;
        Artist result_ar;
        playlist.
    }*/

    /**
     * @return the last existent ID for artist
     * @throws SQLException
     * @throws Exception
     */
    public int getLastIdArtist() throws SQLException, Exception{
        String query="SELECT ID FROM artist";  //creo la query
        return getLId(query);
    }

    /**
     * @return the last existent ID for artist
     * @throws SQLException
     * @throws Exception
     */
    public int getLastIdAlbum() throws SQLException, Exception{
        String query="SELECT ID FROM album";  //creo la query
        return getLId(query);
    }

    /**
     * @return the last existent ID for artist
     * @throws SQLException
     * @throws Exception
     */
    public int getLastIdTrack() throws SQLException, Exception{
        String query="SELECT ID FROM track";  //creo la query
        return getLId(query);
    }


    /**
     * @return the last existent ID for artist
     * @throws SQLException
     * @throws Exception
     */
    private int getLId(String query) throws  SQLException, Exception{
        boolean w;
        String q = query;
        pstatement = con.prepareStatement(query);
        result = pstatement.executeQuery();  //ottengo tutti gli ID degli artisti
        w = result.last();  //muovo il cursore nell'ultima riga
        if(w == false){
            throw new Exception("Non ci sono righe");
        }
        return (int) result.getObject("ID");  //ritorno l'id
    }
}
