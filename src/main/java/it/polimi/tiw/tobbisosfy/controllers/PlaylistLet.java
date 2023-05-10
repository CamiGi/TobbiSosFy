package it.polimi.tiw.tobbisosfy.controllers;

import it.polimi.tiw.tobbisosfy.DAOs.PlaylistDAO;
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

public class PlaylistLet  extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;
    private User u;

    public PlaylistLet(User u) {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PlaylistDAO pd = new PlaylistDAO(connection);
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());

        if(!(req.getParameter("ptitle").isEmpty() || ((ArrayList<Track>)req.getAttribute("songs")).isEmpty())) {

            String playlistTitle = req.getParameter("ptitle");
            ArrayList<Track> songs = (ArrayList<Track>) req.getAttribute("songs");  ///vedere come funziona con html
            Date d = new Date(System.currentTimeMillis());
            Playlist playlist = new Playlist(playlistTitle, d, u);

            int i = -1;

            try {
                pd.addPlaylist(playlist, songs, i);
            } catch (SQLException e){
                ctx.setVariable("error", e.getMessage());
                resp.sendRedirect("/ShowError");
            } catch (Exception e) {
                ctx.setVariable("error", "Something wrong during the add of the playlist in the database");
                resp.sendRedirect("/ShowError");
            }
        }


    }
}
