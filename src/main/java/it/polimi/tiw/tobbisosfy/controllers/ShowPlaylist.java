package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.DAOs.PlaylistDAO;
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
        Playlist playlist;
        PlaylistDAO plFinder = new PlaylistDAO(connection);
        ArrayList<Track> tracks;
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());


        try {
            plID = Integer.parseInt(req.getParameter("playlist"));
            playlist = plFinder.getPlaylistFromId(plID, (User) req.getSession().getAttribute("user"));
            tracks = plFinder.getTracksFromPlaylist(playlist);
        } catch (NumberFormatException e) {
            //redirect pagina errore
            ctx.setVariable("error", "Invalid playlist ID");
            templateEngine.process("/ErrorPage.html", ctx, resp.getWriter());
            return;
        } catch (SQLException e) {
            //redirect pagina errore
            ctx.setVariable("error", "This playlist does not exist or you haven't got the authorization to see it");
            templateEngine.process("/ErrorPage.html", ctx, resp.getWriter());
            return;
        } catch (Exception e) {
            //redirect pagina errore
            ctx.setVariable("error", e.getMessage());
            templateEngine.process("/ErrorPage.html", ctx, resp.getWriter());
            return;
        }

        ctx.setVariable("playlist", playlist);
        ctx.setVariable("tracks", tracks);
        templateEngine.process("/PlaylistPage.html", ctx, resp.getWriter());
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
