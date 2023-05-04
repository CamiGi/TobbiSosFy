package it.polimi.tiw.tobbisosfy.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.tobbisosfy.DAOs.TrackDAO;
import it.polimi.tiw.tobbisosfy.beans.Track;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet ("/StartPlayer")
public class StartPlayer extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;

    public StartPlayer() {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Track track;
        TrackDAO trFinder = new TrackDAO(connection);
        String path;
        final WebContext ctx = DBServletInitializer.createContext(request, response, getServletContext());
        int trID;

        try {
            trID = Integer.parseInt(request.getParameter("track"));
            track = trFinder.getTrack(trID, (String)request.getSession().getAttribute("user"));
        } catch (NumberFormatException e) {
            ctx.setVariable("error", "Erroneous track ID");
            response.sendRedirect("/ShowError");
            return;
        } catch (SQLException e) {
            ctx.setVariable("error", "Track not found");
            response.sendRedirect("/ShowError");
            return;
        } catch (Exception e){
            e.printStackTrace();
            ctx.setVariable("error", e.getMessage());
            response.sendRedirect("/ShowError");
            return;
        }
        path = "/PlayerPage.html";
        ctx.setVariable("track", track);
        templateEngine.process(path, ctx, response.getWriter());
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
