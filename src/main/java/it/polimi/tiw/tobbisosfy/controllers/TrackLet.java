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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class TrackLet extends HttpServlet { //SERVLET DA SPECIFICARE E FARNE UN ALTRA SOLO PER L?ALTRA COSA playlist - track

    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;
    private User u;

    public TrackLet(User u) {
        super();
        this.u = u;
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
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        TrackDAO trackDAO = new TrackDAO(connection);
        getServletContext().getContextPath();
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());
        User u = (User) req.getSession().getAttribute("user");
        ArrayList<Playlist> playlists;
        String path;
        ArrayList<Track> songs = new ArrayList<>();


        try {
             playlists = playlistDAO.getPlaylists(u);
        } catch (SQLException e) {
            throw new RuntimeException(e);  //Redirect pagina errore: mettere a postooooooooooooooooooooo
        }

        try {
            songs = trackDAO.getTracksFromUser(u);
        } catch (SQLException e) {
            throw new RuntimeException(e);  //Redirect pagina errore: mettere a postooooooooooooooooooo
        } catch (Exception e) {
            throw new RuntimeException(e); //Redirect pagina errore: mettere a postooooooooooooooooooo
        }

        path = "/HomePage.html";
        ctx.setVariable("playlists", playlists);
        ctx.setVariable("user", u);
        ctx.setVariable("songs", songs);
        templateEngine.process(path, ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        TrackDAO td = new TrackDAO(connection);
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());

        if(!(req.getParameter("talbum").isEmpty() ||
                req.getParameter("ttitle").isEmpty() ||
                ((Album) req.getAttribute("dalbum")).isEmpty() ||
                ((Genre) req.getAttribute("g")).isEmpty() ||
                req.getParameter("aname").isEmpty())) {

            String trackTitle = req.getParameter("ttitle");
            Date albumDate = (Date) req.getAttribute("dalbum");
            String albumTitle = req.getParameter("talbum");
            Genre albumGenre = (Genre) req.getAttribute("g");
            String artistName = req.getParameter("aname");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(albumDate);

            ///////METTERE A POSTO URIIIIII ///////////////////////////////////////////////
            Artist artist = new Artist(artistName, td);
            Album album = new Album(td, albumTitle, Calendar.YEAR, albumGenre, artist, "uri" );
            Track track = new Track(td, trackTitle, album, "uri", u);

            try {
                td.addTrack(track.getTitle(), track.getAlbum(), track.getMp3Uri(), track.getUser());
            } catch (SQLException e){
                ctx.setVariable("error", e.getMessage());
                resp.sendRedirect("/ShowError");
            } catch (Exception e) {
                ctx.setVariable("error", "Something wrong during the add of the song in the database");
                resp.sendRedirect("/ShowError");
            }
        } else {
            ctx.setVariable("error", "Missing parameters");
            resp.sendRedirect("/ShowError");
        }
    }

    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
