package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.Playlist;
import it.polimi.tiw.tobbisosfy.beans.Track;
import it.polimi.tiw.tobbisosfy.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistDAO {

    private Connection con;
    private TrackDAO td;
    private PreparedStatement ps;
    private ResultSet result;
    private String queryPlID = "SELECT ID FROM playlist WHERE title=? AND userID=?";
    private String queryNewPlaylist = "INSERT INTO playlist VALUES (?, ?, ?)";
    private String queryNewContains = "INSERT INTO contains VALUES(?, ?)";


    public PlaylistDAO(Connection con, TrackDAO td){
        this.con = con;
        this.td=td;

    }

    /**
     *
     * @param playlist
     * @param tracks
     * @param code
     * @return un codice risultato, controllare che sia cambiato, se l'operazione è andata a buon fine deve essere = 1
     * @throws SQLException Vuol dire che già esiste nel DB
     * @throws Exception
     */
    public int addPlaylist(Playlist playlist, ArrayList<Track> tracks, int code) throws SQLException, Exception{

        String queryplst = "SELECT * FROM playlist WHERE title=? AND userID=?";

        ps = con.prepareStatement(queryplst);
        ps.setString(1,playlist.getTitle());
        ps.setString(2,playlist.getUser().getUsername());
        result = ps.executeQuery();

        if(!result.first()){  //se non ho già la playlist
            ps = con.prepareStatement(queryNewPlaylist);
            ps.setString(1,playlist.getTitle());
            ps.setDate(2,playlist.getDate());
            ps.setString(3,playlist.getUser().getUsername());
            code = ps.executeUpdate();
        } else {
            throw new Exception("ATTENZIONE qualcosa non è andato bene: 500");
        }

        ps = con.prepareStatement(queryPlID);
        ps.setString(1,playlist.getTitle());
        ps.setString(2, playlist.getUser().getUsername());
        result = ps.executeQuery();
        int h = result.getInt("ID");

        String querycnts = "SELECT * FROM contains WHERE playlistID=?";

        ps = con.prepareStatement(querycnts);
        ps.setInt(1,h);
        result = ps.executeQuery();

        if(!result.first()){  //se non ho già la tupla

            for (Track t : tracks){
                ps = con.prepareStatement(queryNewContains);
                ps.setInt(1,h);
                try {
                    ps.setInt(2,td.getIDofTrack(t));
                } catch (SQLException e){
                    System.out.println("ATTENZIONE qualcosa non funziona: 501");
                }
                code = ps.executeUpdate();
            }
        } else {
            throw new Exception("ATTENZIONE qualcosa è andato storto: 502");
        }

        return code;
    }

    /**
     *
     * @param user utente che sta interagendo
     * @return un arreylist di tutte le playlist dell'utente
     * @throws SQLException
     */
    public ArrayList<Playlist> getPlaylists(User user) throws  SQLException{
        ArrayList<Playlist> r = new ArrayList<>();
        Playlist pl;

        String query = "SELECT * FROM playlist WHERE title=?";
        ps = con.prepareStatement(query);
        ps.setString(1,user.getUsername());
        result = ps.executeQuery();

        result.first();
        while (!result.isAfterLast()){
            r.add(new Playlist(result.getString("title"), (java.sql.Date) result.getObject("creationDate"), user));
            result.next();
        }

        return r;
    }

    /**
     * Tu mi dai la playlist e io ti restituisco una mappa (ID, Track), così quando scegli la canzone può partire automaticamente il player
     * @param playlist
     * @return
     * @throws SQLException
     */
    public Map<Integer, Track> getTracksFromPlaylist(Playlist playlist) throws SQLException, Exception{
        //DA MARCO PER CAMI: come per la track bisogna fare un controllo anche sull'utente, se no si possono
        //vedere anche le playlist degli altri utenti
        Map<Integer, Track> rs = new HashMap<>();
        ResultSet resultTrack;
        int tid;


        String queryTracks = "SELECT * FROM contains JOIN track WHERE contains.playlistID=? AND contains.trackID=track.ID";  //creo query che seleziona le canzoni (tentativo di JOIN)
        String prova = "SELECT ID FROM playlist WHERE title=?"; //creo query che trova l'id della playlist che mi interessa

        ps = con.prepareStatement(prova);  //settaggio prepared statement
        ps.setString(1,playlist.getTitle());
        result = ps.executeQuery();  //mando la query playlist

        ps = con.prepareStatement(queryTracks);  //settaggio altro statement
        ps.setInt(1,result.getInt("ID"));
        resultTrack = ps.executeQuery();  //mando la query definitiva che mi da tutte le canzoni

        resultTrack.first();
        while (!resultTrack.isAfterLast()){
            tid = resultTrack.getInt("track.ID");
            //rs.add(td.getTrack(tid));  //uso il metodo privato che dato un id di Track restituisce l'oggetto Track
            rs.put(tid, td.getTrack(tid, playlist.getUser().getUsername()));
            resultTrack.next();
        }

        return  rs;
    }

    /**
     * Ritorna l'ID della playlist
     * @param playlist
     * @return -1 se sbagliato, un numero maggiore di zero se giusto (che sarebbe l'id)
     * @throws SQLException
     */
    public int getIdOfPlaylist(Playlist playlist) throws SQLException{
        String queryIdP = "SELECT ID FROM playlist WHERE title=? AND creationDate=? AND userID=?";
        int id = -1;

        ps = con.prepareStatement(queryIdP);
        ps.setString(1,playlist.getTitle());
        ps.setDate(2,playlist.getDate());
        ps.setString(3,playlist.getUser().getUsername());

        result = ps.executeQuery();
        if (result.first()){
            id = result.getInt("ID");
        }

        return id;
    }

    /**
     * Inserisce una canzone nella playlist
     * @param playlist
     * @param track
     * @param code se restituisce 1 è andato a buon fine
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public int addSongToPlaylist(Playlist playlist, Track track, int code) throws SQLException, Exception{
        code = 0;
        int idp = -1;
        int idt = -1;

        idp = this.getIdOfPlaylist(playlist);
        idt = td.getIDofTrack(track);

        String query1 = "SELECT * FROM contains WHERE playlistID=? AND trackID=?";
        String query2 = "INSERT INTO contains VALUES(?, ?)";

        ps = con.prepareStatement(query1);
        ps.setInt(1, idp);
        ps.setInt(2, idt);
        result = ps.executeQuery();

        if(!result.first()){
            ps = con.prepareStatement(query2);
            ps.setInt(1, idp);
            ps.setInt(2, idt);
            code = ps.executeUpdate();
        } else {
            throw new Exception("ATTENZIONE la canzone è già nella playlist: "+playlist.getTitle());
        }
        return code;
    }
}
