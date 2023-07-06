package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.*;

import java.sql.*;
import java.util.ArrayList;


public class TrackDAO {
    private final Connection con;
    private ResultSet result;
    private PreparedStatement pstatement = null;
    private PreparedStatement ps;

    public TrackDAO(Connection con){
        this.con=con;
    }

    /**
     * Aggiungo una track nel db
     * @param user
     * @throws SQLException
     * @throws Exception
     */
    public void addTrack(String title, Album album, String mp3Uri, User user) throws SQLException, Exception {
        int rescode;
        String queryArID = "SELECT ID, name FROM artist WHERE name= ?";
        pstatement = con.prepareStatement(queryArID);
        pstatement.setString(1, album.getArtist().getArtistName());
        result = pstatement.executeQuery();

        if(!result.isBeforeFirst()){
            rescode = newArtist(album.getArtist());
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 100");
            }
            rescode = newAlbum(album);
            if(rescode != 1){
                throw new Exception("ATTENZIONE quaclosa non è andato bene : 101");
            }
            rescode = newTrack(title, album, mp3Uri, user);
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 102");
            }

        } else {

            String queryAlID = "SELECT ID, name FROM album WHERE name= ?";
            pstatement = con.prepareStatement(queryAlID);
            pstatement.setString(1, album.getTitle());
            result = pstatement.executeQuery();
            if(!result.isBeforeFirst()){
                rescode = newAlbum(album);
                if(rescode != 1){
                    throw new Exception("ATTENZIONE quaclosa non è andato bene : 200");
                }
                rescode = newTrack(title, album, mp3Uri, user);
                if(rescode != 1){
                    throw new Exception("ATTENZIONE qualcosa non è andato bene : 201");
                }

            } else {

                String queryTID = "SELECT ID, title FROM track WHERE title= ?";
                pstatement = con.prepareStatement(queryTID);
                pstatement.setString(1, title);
                result = pstatement.executeQuery();

                if(!result.isBeforeFirst()){
                    rescode = newTrack(title, album, mp3Uri, user);
                    if(rescode != 1){
                        throw new Exception("ATTENZIONE qualcosa non è andato bene : 300");
                    }
                } else {
                    throw new Exception("ATTENZIONE LA CANZONE è GIà PRESENTE NEL DATABASE");
                }
            }
        }
    }

    private int newArtist(Artist artist) throws SQLException{
        int code;
        try{
            String queryNewArtist = "INSERT INTO artist VALUES (?, ?)";
            ps = con.prepareStatement(queryNewArtist);
            ps.setString(1, null);
            ps.setString(2, artist.getArtistName());
            code = ps.executeUpdate();  //returns the number of rows inserted into the table
        } catch (SQLException e){
            throw new SQLException(e);
        }
        return code;
    }

    //creo album: name, year, genre, artistID, img
    private int newAlbum(Album album) throws SQLException{
        int code;
        try{
            ps=con.prepareStatement("SELECT ID FROM artist WHERE name=?");
            ps.setString(1,album.getArtist().getArtistName());
            ResultSet re =ps.executeQuery();
            re.next();
            String queryNewAlbum = "INSERT INTO album VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(queryNewAlbum);
            ps.setString(1, null);
            ps.setString(2, album.getTitle());
            ps.setInt(3, album.getYear());
            ps.setString(4, album.getGenre().toString());
            ps.setInt(5, re.getInt("ID"));
            ps.setString(6, album.getImgUri());
            code = ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException(e);
        }
        return code;
    }

    private int newTrack(String title, Album album, String mp3Uri, User user) throws SQLException{
        int code;
        try{
            ps=con.prepareStatement("SELECT ID FROM album WHERE name=?");
            ps.setString(1, album.getTitle());
            ResultSet re =ps.executeQuery();
            ps=con.prepareStatement("SELECT username FROM user WHERE username=?");
            ps.setString(1,user.getUsername());
            ResultSet re1 =ps.executeQuery();

            String queryNewTrack = "INSERT INTO track VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(queryNewTrack);
            ps.setString(1, null);
            ps.setString(2, title);
            re.next();
            re1.next();
            ps.setInt(3, re.getInt("ID"));
            ps.setString(4, mp3Uri);
            ps.setString(5, re1.getString("username"));
            code = ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException(e);
        }
        return code;
    }

    public ArrayList<Track> getTracksFromUser(User user) throws SQLException, Exception{

        ArrayList<Track> tracks = new ArrayList<>();
        String query = "SELECT ID FROM track WHERE username=?";

        ps = con.prepareStatement(query);
        ps.setString(1,user.getUsername());
        ResultSet r = ps.executeQuery();

        if(r.isBeforeFirst()){
            r.next();
            while(!r.isAfterLast()){
                tracks.add(this.getTrack(r.getInt("ID"), user.getUsername()));
                r.next();
            }
        } else {
            return new ArrayList<>();
        }

        return tracks;
    }

    /**
     * Restituisce l'oggetto track dato l'id (int)
     * @param trackID
     * @return
     * @throws SQLException
     */
    public Track getTrack(int trackID, String username) throws SQLException, Exception{

        Track t;
        Album album;
        Artist artist;
        User u;
        ResultSet resultTrack;
        ResultSet resultAlbum;

        String queryTrack = "SELECT * FROM track WHERE ID=? AND username=?";
        ps = con.prepareStatement(queryTrack);
        ps.setInt(1,trackID);
        ps.setString(2, username);
        resultTrack = ps.executeQuery();

        if (!resultTrack.isBeforeFirst()){
            throw new Exception("ATTENTION! This track does not belong to you");
        }
        resultTrack.next();

        String queryAlbum = "SELECT * FROM album WHERE ID=?";
        ps = con.prepareStatement(queryAlbum);
        ps.setInt(1, resultTrack.getInt("albumID"));
        resultAlbum = ps.executeQuery();
        resultAlbum.next();

        String queryArtist = "SELECT * FROM artist WHERE ID=?";
        ps = con.prepareStatement(queryArtist);
        ps.setInt(1,resultAlbum.getInt("artistID"));
        result = ps.executeQuery();
        result.next();

        artist = new Artist(result.getString("name"));
        album = new Album(resultAlbum.getString("name"), resultAlbum.getInt("year"), Genre.valueOf(resultAlbum.getString("genre")), artist, resultAlbum.getString("img"));
        String queryUser = "SELECT * FROM user WHERE username=?";
        ps = con.prepareStatement(queryUser);
        ps.setString(1,resultTrack.getString("username"));
        result = ps.executeQuery();
        result.next();
        u = new User(result.getString("username"), result.getString("password"));
        t = new Track(resultTrack.getString("title"), album, resultTrack.getString("file"), u);
        t.setId(trackID);

        return t;

    }
}
