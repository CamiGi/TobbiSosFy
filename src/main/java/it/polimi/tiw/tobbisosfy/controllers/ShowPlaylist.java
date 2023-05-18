package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.DAOs.PlaylistDAO;
import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;
import it.polimi.tiw.tobbisosfy.beans.Playlist;
import it.polimi.tiw.tobbisosfy.beans.Track;
import it.polimi.tiw.tobbisosfy.beans.User;
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
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/ShowPlaylist")
public class ShowPlaylist extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;

    public ShowPlaylist() {
        super();
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
        int plID;
        int group;
        boolean next;
        PlaylistDAO plFinder = new PlaylistDAO(connection);
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());
        User user = (User) req.getSession().getAttribute("user");
        Playlist playlist;
        ArrayList<Track> tracks;
        ArrayList<Track> shownTracks;
        ArrayList<Track> addableTracks;
        String error = getServletContext().getContextPath() + "/ShowError?error=";

        System.out.println("Start searching for playlist");
        try {
            plID = Integer.parseInt(req.getParameter("playlist"));
            playlist = plFinder.getPlaylistFromId(plID, user);
            tracks = plFinder.getTracksFromPlaylist(playlist);
            addableTracks = new TrackDAO(connection).getTracksFromUser(user);
        } catch (NumberFormatException e) {
            System.out.println("Invalid playlist ID");
            error += "Invalid playlist ID";
            resp.sendRedirect(error);
            return;
        } catch (SQLException e) {
            System.out.println("This playlist does not exist or you haven't got the authorization to see it");
            error += "This playlist does not exist or you haven't got the authorization to see it";
            resp.sendRedirect(error);
            return;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            error += e.getMessage();
            resp.sendRedirect(error);
            return;
        }

        for (int i=addableTracks.size()-1; i>=0; i--) {
            for (Track t : tracks) {
                if (t.getId() == addableTracks.get(i).getId()) {
                    addableTracks.remove(i);
                    break;
                }
            }
        }
        group = 5*Integer.parseInt(req.getParameter("group"));//////////
        shownTracks = new ArrayList<>(5);

        for (int c=group; c<group+5 && c<tracks.size(); c++)
            shownTracks.add(tracks.get(c));

        System.out.println(shownTracks.size() + " tracce mostrate");
        next = group+5<tracks.size()-1;

        ctx.setVariable("playlist", playlist);
        ctx.setVariable("tracks", shownTracks);
        ctx.setVariable("addTrks", addableTracks);
        ctx.setVariable("group", group);
        ctx.setVariable("next", next);
        System.out.println("Rendering playlist page");
        System.out.println("'C:'"+shownTracks.get(0).getMp3Uri());
        templateEngine.process("/PlaylistPage.html", ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] tracks = req.getParameterValues("tracks");
        ArrayList<Integer> trIDs;
        PlaylistDAO plfinder = new PlaylistDAO(connection);
        Playlist playlist;
        String path = getServletContext().getContextPath();
        String error = path + "/ShowError?error=";

        try {
            playlist = plfinder.getPlaylistFromId(Integer.parseInt(req.getParameter("playlist")),
                    (User) req.getSession().getAttribute("user"));
        } catch (NumberFormatException e) {
            error += "Invalid playlist ID";
            resp.sendRedirect(error);
            return;
        } catch (SQLException e) {
            error += "Playlist cannot be found or you haven't got the rights to see it";
            resp.sendRedirect(error);
            return;
        } catch (Exception e) {
            error += e.getMessage();
            resp.sendRedirect(error);
            return;
        }

        if (tracks == null) {
            error += "Add at least one song to the playlist";
            resp.sendRedirect(error);
            return;
        }
        trIDs = new ArrayList<>();
        System.out.println("SONO QUA");

        try {
            for (String track : tracks) {
                trIDs.add(Integer.parseInt(track));  //eccezione qua
            }
            plfinder.addSongsToPlaylist(playlist, trIDs);
        } catch (NumberFormatException e) {
            error += "The song you're trying to add does not exist or you haven't the authorization to see it";
            resp.sendRedirect(error);
            return;
        } catch (Exception e) {
            error += e.getMessage();
            resp.sendRedirect(error);
            return;
        }

        System.out.println("SONO QUIII");

        resp.sendRedirect("ShowPlaylist?playlist="+playlist.getId()+"&group=0");
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
