package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.DAOs.PlaylistDAO;
import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;
import it.polimi.tiw.tobbisosfy.beans.*;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/PLinsert")
public class PlaylistLet  extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private User u;

    public PlaylistLet() {
        super();
    }

    private void setU(User user){
        this.u = user;
    }

    @Override
    public void init() throws ServletException {
        try {
            connection = DBServletInitializer.init(getServletContext());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnavailableException("Couldn't get db connection");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.setU((User) req.getSession().getAttribute("user"));
        PlaylistDAO pd = new PlaylistDAO(connection);
        TrackDAO td = new TrackDAO(connection);
        String error = req.getContextPath() + "/ShowError?error=";
        String ctxPath = req.getContextPath();
        //System.out.println(req.getParameterValues("song"));
        String[] songs = req.getParameterValues("song");

        if(!(req.getParameter("ptitle").isEmpty() || songs == null )  ) {

            String playlistTitle = req.getParameter("ptitle");
            System.out.println("Titolo preso");
            ArrayList<Track> sng = new ArrayList<>();

            for (String song : songs) {
                try {
                    sng.add(td.getTrack(Integer.parseInt(song), u.getUsername()));
                } catch (Exception e) {
                    error += "Something wrong during the add of the playlist in the database";
                    resp.sendRedirect(error);
                    return;
                }
            }

            System.out.println("Canzoni prese");

            Date d = new Date(System.currentTimeMillis());
            Playlist playlist = new Playlist(playlistTitle, d, u);

            int i = -1;

            try {
                System.out.println("Invio nuova playlist");
                pd.addPlaylist(playlist, sng);
                System.out.println("Inviata nuova playlist");
            } catch (SQLException e){
                e.printStackTrace();
                error += "Error occurred while saving the playlist in the database (SQL exception)";
                resp.sendRedirect(error);
                return;
            } catch (Exception e) {
                error += "Error occurred while saving the playlist in the database";
                resp.sendRedirect(error);
                return;
            }
        } else {
            error += "Missing parameters in the 'Add a new playlist' form";
            resp.sendRedirect(error);
            return;
        }
        System.out.println("è andato tutto bene");
        resp.sendRedirect(ctxPath+"/Home");
    }
}
