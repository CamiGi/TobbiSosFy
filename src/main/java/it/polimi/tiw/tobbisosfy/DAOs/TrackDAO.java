package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.*;

import java.sql.*;


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
    private String queryAlbum = "SELECT * FROM album WHERE ID=?";
    private String queryArtist = "SELECT * FROM artist WHERE ID=?";
    private String queryTrack = "SELECT * FROM track WHERE ID=?";
    private String queryUser = "SELECT * FORM user WHERE username=?";

    public TrackDAO(Connection con){
        this.con=con;
    }

    public void addTrack(Track track, User user) throws SQLException, Exception {
        int rescode = 0;
        pstatement = con.prepareStatement(queryArID);   //vedo se ho l'artista
        pstatement.setString(1,track.getAlbum().getArtist().getName());
        result = pstatement.executeQuery();  //result set con una riga sola se l'artista esiste, se non esiste non ho nessuna riga
        if(!result.first()){  //se non ho nessun artista
            rescode = newArtist(rescode, track.getAlbum().getArtist());  //creo artista: name
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 100");
            }
            rescode = newAlbum(rescode, track.getAlbum());  //creo album: creo album: name, year, genre, artistID, img
            if(rescode != 1){
                throw new Exception("ATTENZIONE quaclosa non è andato bene : 101");
            }
            rescode = newTrack(rescode, track, user);  //creo track: title, albumID, file, username
            if(rescode != 1){
                throw new Exception("ATTENZIONE qualcosa non è andato bene : 102");
            }
        } else {  //se ho già l'artista
            pstatement = con.prepareStatement(queryAlID);  //vedo se esite l'album
            pstatement.setString(1, track.getAlbum().getName());
            result = pstatement.executeQuery();  //mando la query
            if(!result.first()){  //se non esiste l'album
                rescode = newAlbum(rescode, track.getAlbum());  //creo album: name, year, genre, artistID, img
                if(rescode != 1){
                    throw new Exception("ATTENZIONE quaclosa non è andato bene : 200");
                }
                rescode = newTrack(rescode, track, user);  //creo track: title, albumID, file, username
                if(rescode != 1){
                    throw new Exception("ATTENZIONE qualcosa non è andato bene : 201");
                }
            } else {  //caso in cui esiste l'artista e l'album
                pstatement = con.prepareStatement(queryTID);  //vedo se esiste la track
                pstatement.setString(1,track.getTitle());
                result = pstatement.executeQuery();
                if(!result.first()){  // la track non esiste
                    rescode = newTrack(rescode, track, user);  //creo track
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

    public int getIDofTrack(Track track) throws  SQLException{
        ps = con.prepareStatement("SELECT ID FROM track WHERE title=? AND username=?");
        ps.setString(1,track.getTitle());
        ps.setString(2,track.getUser().getUsername());
        result = ps.executeQuery();
        return result.getInt("ID");
    }

    /**
     * Restituisce l'oggetto track dato l'id
     * @param trackID
     * @return
     * @throws SQLException
     */
    public Track getTrack(int trackID) throws SQLException {

        Track t;
        Album album;
        Artist artist;
        User user;
        ResultSet resultTrack;
        ResultSet resultAlbum;
        int marco = -1;

        //query per ricevere le info della track
        ps = con.prepareStatement(queryTrack);
        ps.setInt(1,trackID);
        resultTrack = ps.executeQuery();

        //query per ricevere le info sull'album
        ps = con.prepareStatement(queryAlbum);
        marco = resultTrack.getInt("albumID");
        ps.setInt(1, marco); ///PROBLEMA
        resultAlbum = ps.executeQuery();

        //query per ricevere le info sull'artista
        ps = con.prepareStatement(queryArtist);
        ps.setInt(1,resultAlbum.getInt("artistID"));
        result = ps.executeQuery();

        //costruisco la track
        artist = new Artist(result.getString("name"),this);
        album = new Album(this, resultAlbum.getString("name"), resultAlbum.getInt("year"),Genre.valueOf(resultAlbum.getString("genre")), artist, resultAlbum.getString("img"));
        ps = con.prepareStatement(queryUser);
        ps.setString(1,resultTrack.getString("username"));
        result = ps.executeQuery();
        user = new User(result.getString("username"), result.getString("password"));
        t = new Track(this, resultTrack.getString("title"), album, resultTrack.getString("file"), user);

        //ritorno la track
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
