package it.polimi.tiw.tobbisosfy.DAOs;

import it.polimi.tiw.tobbisosfy.beans.Playlist;
import it.polimi.tiw.tobbisosfy.beans.Track;
import it.polimi.tiw.tobbisosfy.beans.User;

import java.lang.management.PlatformLoggingMXBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistDAO {

    TrackDAO td;
    private final Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private final String queryPlID = "SELECT ID FROM playlist WHERE title=? AND userID=?";
    private final String queryNewPlaylist = "INSERT INTO playlist VALUES (?, ?, ?)";
    private final String queryNewContains = "INSERT INTO contains VALUES(?, ?)";


    public PlaylistDAO(Connection con){
        this.con = con;
        td = new TrackDAO(con);
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
    public void addPlaylist(Playlist playlist, ArrayList<Track> tracks, int code) throws SQLException, Exception{

        String queryplst = "SELECT * FROM playlist WHERE title=? AND userID=?";

        ps = con.prepareStatement(queryplst);
        ps.setString(1,playlist.getTitle());
        ps.setString(2,playlist.getUser().getUsername());
        result = ps.executeQuery();

        if(!result.isBeforeFirst()){  //se non ho già la playlist
            ps = con.prepareStatement(queryNewPlaylist);
            ps.setString(1,playlist.getTitle());
            ps.setDate(2,playlist.getDate());
            ps.setString(3,playlist.getUser().getUsername());
            code = ps.executeUpdate();
        } else {
            throw new Exception("ATTENZIONE qualcosa non è andato bene: 500");
        }

        if (!(code == 1)){
            con.rollback();
            throw new Exception("ATTENZIONE qualcosa è andato storto: 501");
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

        if(!result.isBeforeFirst()){  //se non ho già la tupla

            for (Track t : tracks){
                ps = con.prepareStatement(queryNewContains);
                ps.setInt(1,h);
                try {
                    ps.setInt(2, t.getId());
                } catch (SQLException e){
                    System.out.println("ATTENZIONE qualcosa non funziona: 502");
                }
                code = ps.executeUpdate();
            }
        } else {
            throw new Exception("ATTENZIONE qualcosa è andato storto: 503");
        }
        if (!(code == 1)){
            con.rollback();
            throw new Exception("ATTENZIONE qualcosa è andato storto: 504");
        }
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

        String query = "SELECT * FROM playlist WHERE userID=?";
        ps = con.prepareStatement(query);
        ps.setString(1,user.getUsername());
        result = ps.executeQuery();

        result.next();
        
        while (!result.isAfterLast()){
            pl= new Playlist(result.getString("title"), (java.sql.Date) result.getObject("creationDate"), user);
            pl.setId(result.getInt("ID"));
            r.add(pl);
            result.next();
        }
        return r;
    }

    public boolean isTrackContained(Track track, Playlist playlist) throws SQLException{
        String query = "SELECT * FROM contains WHERE playlistID=? AND trackID=?";
        ps = con.prepareStatement(query);
        ps.setInt(1, playlist.getId());
        ps.setInt(2, track.getId());
        result = ps.executeQuery();

        if(result.isBeforeFirst()){
            return true;
        } else if (!result.isBeforeFirst()) {
            return false;
        }
        return false;
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

        resultTrack.next();
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
        if (result.isBeforeFirst()){
            id = result.getInt("ID");
        }

        return id;
    }

    /**
     * Inserisce una canzone nella playlist
     * @param playlist
     * @param track
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public void addSongToPlaylist(Playlist playlist, Track track) throws SQLException, Exception{
        int code = 0;
        int idp = -1;
        int idt = -1;

        idp = this.getIdOfPlaylist(playlist);
        idt = track.getId();

        String query1 = "SELECT * FROM contains WHERE playlistID=? AND trackID=?";
        String query2 = "INSERT INTO contains VALUES(?, ?)";

        ps = con.prepareStatement(query1);
        ps.setInt(1, idp);
        ps.setInt(2, idt);
        result = ps.executeQuery();

        if(!result.isBeforeFirst()){
            ps = con.prepareStatement(query2);
            ps.setInt(1, idp);
            ps.setInt(2, idt);
            code = ps.executeUpdate();
        } else {
            throw new Exception("ATTENZIONE la canzone è già nella playlist: "+playlist.getTitle());
        }
        if (!(code == 1)){
            con.rollback();
            throw new Exception("ATTENZIONE qualcosa è andato storto: 505");
        }
    }

    public Playlist getPlaylistFromId(int id, User user) throws SQLException, Exception {

        String query = "SELECT * FROM playlist WHERE ID=? AND userID=?";
        Playlist plst;

        ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ps.setString(2, user.getUsername());

        result = ps.executeQuery();
        result.next();

        if (user.getUsername().equals(result.getString("userID"))) {
            plst = new Playlist(result.getString("title"), result.getDate("creationDate"), user);
        } else {
            throw new Exception("ATTENZIONE la playlist selezionata non appartiene all'utente: 700");
        }
        plst.setId(id);

        return plst;
    }
}
