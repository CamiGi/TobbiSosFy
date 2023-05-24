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
        pstatement = con.prepareStatement(queryArID);   //vedo se ho l'artista
        pstatement.setString(1, album.getArtist().getArtistName());
        result = pstatement.executeQuery();  //result set con una riga sola se l'artista esiste, se non esiste non ho nessuna riga
        if(!result.isBeforeFirst()){  //se non ho nessun artista
            rescode = newArtist(album.getArtist());  //creo artista: name
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 100");
            }
            rescode = newAlbum(album);  //creo album: creo album: name, year, genre, artistID, img
            if(rescode != 1){
                throw new Exception("ATTENZIONE quaclosa non è andato bene : 101");
            }
            rescode = newTrack(title, album, mp3Uri, user);  //creo track: title, albumID, file, username
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 102");
            }
        } else {  //se ho già l'artista
            String queryAlID = "SELECT ID, name FROM album WHERE name= ?";
            pstatement = con.prepareStatement(queryAlID);  //vedo se esite l'album
            pstatement.setString(1, album.getTitle());
            result = pstatement.executeQuery();  //mando la query
            if(!result.isBeforeFirst()){  //se non esiste l'album
                rescode = newAlbum(album);  //creo album: name, year, genre, artistID, img
                if(rescode != 1){
                    throw new Exception("ATTENZIONE quaclosa non è andato bene : 200");
                }
                rescode = newTrack(title, album, mp3Uri, user);  //creo track: title, albumID, file, username
                if(rescode != 1){
                    throw new Exception("ATTENZIONE qualcosa non è andato bene : 201");
                }
            } else {  //caso in cui esiste l'artista e l'album
                String queryTID = "SELECT ID, title FROM track WHERE title= ?";
                pstatement = con.prepareStatement(queryTID);  //vedo se esiste la track
                pstatement.setString(1, title);
                result = pstatement.executeQuery();
                if(!result.isBeforeFirst()){  // la track non esiste
                    rescode = newTrack(title, album, mp3Uri, user);  //creo track
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
    private int newArtist(Artist artist) throws SQLException{
        int code;
        try{
            //NULL, name
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
            ps=con.prepareStatement("SELECT ID FROM artist WHERE name=?");  //chiedo al db l'id dell'artista col nome che compare nell'oggetto album
            ps.setString(1,album.getArtist().getArtistName());
            ResultSet re =ps.executeQuery();
            re.next();
            //NULL, name, year, genre, artistID, img
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

    //creo track: title, albumID, file, username
    private int newTrack(String title, Album album, String mp3Uri, User user) throws SQLException{
        int code;
        try{
            ps=con.prepareStatement("SELECT ID FROM album WHERE name=?");  //chiedo al db l'id dell'album col nome che compare nell'oggetto track
            ps.setString(1, album.getTitle());
            ResultSet re =ps.executeQuery();
            ps=con.prepareStatement("SELECT username FROM user WHERE username=?");  //chiedo al db l'id dell'album col nome che compare nell'oggetto track
            ps.setString(1,user.getUsername());
            ResultSet re1 =ps.executeQuery();
            //NULL, title, albumID, file, username
            String queryNewTrack = "INSERT INTO track VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(queryNewTrack);
            ps.setString(1, null);
            ps.setString(2, title);
            re.next();
            re1.next();
            ps.setInt(3, re.getInt("ID"));
            ps.setString(4, mp3Uri);
            ps.setString(5, re1.getString("username"));  //ridondante ma almeno se c'è un errore nel db viene lanciato
            code = ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException(e);
        }
        return code;
    }

    /*
    /**
     * Data la track restituisce il suo id (int)
     * @param track
     * @return
     * @throws SQLException

    public int getIDofTrack(Track track) throws  SQLException{
        ps = con.prepareStatement("SELECT ID FROM track WHERE title=? AND username=?");
        ps.setString(1,track.getTitle());
        ps.setString(2,track.getUser().getUsername());
        result = ps.executeQuery();
        return result.getInt("ID");
    }*/

    public ArrayList<Track> getTracksFromUser(User user) throws SQLException, Exception{

        ArrayList<Track> tracks = new ArrayList<>();
        String query = "SELECT ID FROM track WHERE username=?";

        ps = con.prepareStatement(query);
        ps.setString(1,user.getUsername());
        ResultSet r = ps.executeQuery();

        if(r.isBeforeFirst()){
            r.next();
            while(!r.isAfterLast()){
                System.out.println(r.getInt("ID"));
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

        //query per ricevere le info della track
        String queryTrack = "SELECT * FROM track WHERE ID=? AND username=?";
        ps = con.prepareStatement(queryTrack);
        ps.setInt(1,trackID);
        ps.setString(2, username);
        resultTrack = ps.executeQuery();

        if (!resultTrack.isBeforeFirst()){
            throw new Exception("ATTENTION! This track does not belong to you");
        }
        resultTrack.next();

        //query per ricevere le info sull'album
        String queryAlbum = "SELECT * FROM album WHERE ID=?";
        ps = con.prepareStatement(queryAlbum);
        ps.setInt(1, resultTrack.getInt("albumID"));
        resultAlbum = ps.executeQuery();
        resultAlbum.next();

        //query per ricevere le info sull'artista
        String queryArtist = "SELECT * FROM artist WHERE ID=?";
        ps = con.prepareStatement(queryArtist);
        ps.setInt(1,resultAlbum.getInt("artistID"));
        result = ps.executeQuery();
        result.next();

        //costruisco la track
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
        pstatement = con.prepareStatement(query);
        result = pstatement.executeQuery();  //ottengo tutti gli ID degli artisti
        w = result.last();  //muovo il cursore nell'ultima riga
        if(!w){
            throw new Exception("Non ci sono righe");
        }
        return (int) result.getObject("ID");  //ritorno l'id
    }
}
