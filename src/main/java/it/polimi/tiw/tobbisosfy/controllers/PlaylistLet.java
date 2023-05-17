package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.DAOs.PlaylistDAO;
import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;
import it.polimi.tiw.tobbisosfy.beans.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
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
    private TemplateEngine templateEngine;
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
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        System.out.println("PlaylistLet inizializzata");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.setU((User) req.getSession().getAttribute("user"));
        PlaylistDAO pd = new PlaylistDAO(connection);
        TrackDAO td = new TrackDAO(connection);
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());
        String error = req.getContextPath() + "/ShowError?error=";
        String ctxPath = req.getContextPath();
        //System.out.println(req.getParameterValues("song"));
        String[] songs = req.getParameterValues("song");

        if(!(req.getParameter("ptitle").isEmpty() || songs[0].isEmpty() )  ) {  ////METTI A POSTO

            String playlistTitle = req.getParameter("ptitle");
            System.out.println("Titolo preso");
            ArrayList<Track> sng = new ArrayList<>();  ///vedere come funziona con html

            for(int i = 0; i<songs.length; i++){
                try {
                    sng.add(td.getTrack(Integer.parseInt(songs[i]), u.getUsername()));
                } catch (Exception e) {
                    error += "Something wrong during the add of the playlist in the database"; //Messaggio d'errore, per CAMIIIIIII
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
                pd.addPlaylist(playlist, sng, i);
                System.out.println("Inviata nuova playlist");
            } catch (SQLException e){
                error += e.getMessage(); //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            } catch (Exception e) {
                error += "Something wrong during the add of the playlist in the database"; //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            }
        }
        System.out.println("è andato tutto bene");
        resp.sendRedirect(ctxPath+"/Home");
    }
}
