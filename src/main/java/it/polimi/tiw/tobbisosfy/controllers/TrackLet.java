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
import javax.servlet.http.Part;
import javax.sound.sampled.AudioFileFormat;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

@WebServlet("/Home")
public class TrackLet extends HttpServlet { //SERVLET DA SPECIFICARE E FARNE UN ALTRA SOLO PER L?ALTRA COSA playlist - track

    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;
    private User u;
    private String audioFP = "";
    private String imgFP = "";

    public TrackLet() {
        //System.out.println("------- inizio costruttore tracklet -------");
        super();
        System.out.println("------- fine costruttore tracklet -------");
    }

    @Override
    public void init() throws ServletException {
        System.out.println("HEY");
        try {
            System.out.println("SONO QUAAA");
            connection = DBServletInitializer.init(getServletContext());
            System.out.println("SONO QUIII");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnavailableException("Couldn't get db connection");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("1");
        ServletContext servletContext = getServletContext();
        System.out.println("2");
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        System.out.println("3");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        System.out.println("4");
        this.templateEngine = new TemplateEngine();
        System.out.println("5");
        this.templateEngine.setTemplateResolver(templateResolver);
        System.out.println("6");
        templateResolver.setSuffix(".html");
        imgFP = getServletContext().getInitParameter("imagepath");
        audioFP = getServletContext().getInitParameter("trackpath");
    }

    private void setU(User user){
        this.u = user;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        TrackDAO trackDAO = new TrackDAO(connection);
        getServletContext().getContextPath();
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());
        this.setU((User) req.getSession().getAttribute("user"));  //setto lo user che mi serve per tutta la classe
        ArrayList<Playlist> playlists;
        String path;
        String error = req.getContextPath() + "/ShowError?error=";
        ArrayList<Track> songs = new ArrayList<>();

        try {
            playlists = playlistDAO.getPlaylists(u);
        } catch (SQLException e) {
            error += ""; //Messaggio d'errore, per CAMIIIIIII
            resp.sendRedirect(error);
            return;
        }

        try {
            songs = trackDAO.getTracksFromUser(u);
        } catch (SQLException e) {
            error += ""; //Messaggio d'errore, per CAMIIIIIII
            resp.sendRedirect(error);
            return;
        } catch (Exception e) {
            error += ""; //Messaggio d'errore, per CAMIIIIIII
            resp.sendRedirect(error);
            return;
        }

        path = "/HomePage.html";
        ctx.setVariable("playlists", playlists);
        System.out.println(playlists);
        ctx.setVariable("user", u);
        System.out.println(u);
        ctx.setVariable("songs", songs);
        System.out.println(songs);
        templateEngine.process(path, ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("CIAO CAMIIII");
        TrackDAO td = new TrackDAO(connection);
        final WebContext ctx = DBServletInitializer.createContext(req, resp, getServletContext());
        String ctxPath = req.getContextPath();
        String error = ctxPath + "/ShowError?error=";

        String trackTitle = req.getParameter("ttitle");
        System.out.println("ttitle preso");
        int albumDate = 0;
        albumDate = Integer.parseInt(req.getParameter("dalbum"));  //
        //albumDate = req.getInteger("dalbum"); ok
        System.out.println("dalbum preso");
        String albumTitle = req.getParameter("talbum");
        System.out.println("talbum preso");
        Genre albumGenre = (Genre) req.getAttribute("g");
        System.out.println("g preso");
        String artistName = req.getParameter("aname");
        System.out.println("aname preso");

        /* trackTitle = null;
        Part albumDate = null;
        Part albumTitle = null;
        Part albumGenre = null;
        Part artistName = null;*/
        Part taudio = null;
        Part img = null;

        try {
            System.out.println("DENTRO AL TRY");
            //String trackTitle = req.getParameter("ttitle");
            //trackTitle = req.getPart("ttitl");
            /*trackTitle = req.getParameter("ttitle");
            System.out.println("ttitle preso");
            albumDate = req.getPart("dalbum");  //
            //albumDate = req.getInteger("dalbum");
            System.out.println("dalbum preso");
            albumTitle = req.getPart("talbum");
            System.out.println("talbum preso");
            albumGenre = req.getPart("g");
            System.out.println("g preso");
            artistName = req.getPart("aname");
            System.out.println("aname preso");*/
            taudio = req.getPart("audio");
            img = req.getPart("img");
            System.out.println("STO USCENDO DAL TRY");
        } catch (Exception e) {
            e.printStackTrace();
            error += ""; //Messaggio d'errore, per CAMIIIIIII
            resp.sendRedirect(error);
            return;
        }

        System.out.println("JJJJJJ");

        if(!(/*albumTitle == null || albumTitle.getSize() <= 0 ||
                trackTitle == null || trackTitle.getSize() <= 0 ||
                albumDate == null || albumDate.getSize() <= 0 ||
                albumGenre == null || albumGenre.getSize() <= 0 ||
                artistName == null || artistName.getSize() <= 0 ||*/
                albumTitle.isEmpty() || trackTitle.isEmpty() ||
                albumDate == 0 || albumGenre.isEmpty() ||
                artistName.isEmpty() ||
                img == null || img.getSize() <= 0 ||
                taudio == null || taudio.getSize() <= 0)) {

            // We first check the parameter needed is present
            /*if (img == null || img.getSize() <= 0) {
                ctx.setVariable("error", "Missing image album in request!");
                resp.sendRedirect(ctxPath + "/ShowError");
                return;
            }
            System.out.println("HHHHH");
            // We first check the parameter needed is present
            if (taudio == null || taudio.getSize() <= 0) {
                ctx.setVariable("error", "Missing the file audio in request!");
                resp.sendRedirect(ctxPath + "/ShowError");
                return;
            }*/

            //String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            /*String aTitle = new String(albumTitle.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("talbum preso");
            String tTitle = new String(trackTitle.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("ttitle preso");
            String aName = new String(artistName.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("aname preso");
            String adate = new String(albumDate.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int aDate = Integer.parseInt(adate);
            System.out.println("dalbum preso");
            String agenre = new String(albumDate.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            Genre aGenre = Genre.valueOf(agenre);
            System.out.println("g preso");*/

            System.out.println("AAAA");

            // We then check the parameter is valid (in this case right format)
            String contentTypeImg = img.getContentType();
            System.out.println("Type " + contentTypeImg);

            if (!contentTypeImg.startsWith("image")) {
                error += ""; //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            }


            // We then check the parameter is valid (in this case right format)
            String contentTypeAudio = taudio.getContentType();
            System.out.println("Type " + contentTypeAudio);

            if (!contentTypeImg.startsWith("audio")) {
                ctx.setVariable("error", "Audio file format not permitted!");
                resp.sendRedirect(ctxPath + "/ShowError");
                return;
            }

            System.out.println("BBBBB");

            String imgName = Paths.get(img.getSubmittedFileName()).getFileName().toString();
            System.out.println("Filename: " + imgName);

            String audioName = Paths.get(taudio.getSubmittedFileName()).getFileName().toString();
            System.out.println("Filename: " + audioName);

            String imgOutputPath = imgFP + imgName; //folderPath inizialized in init
            System.out.println("Output path: " + imgOutputPath);

            File imgFile = new File(imgOutputPath);

            String audioOutputPath = audioFP + imgName; //folderPath inizialized in init
            System.out.println("Output path: " + audioOutputPath);

            File audioFile = new File(audioOutputPath);

            System.out.println("CCCC");

            try (InputStream imgContent = img.getInputStream()) {
                // TODO: WHAT HAPPENS IF A FILE WITH THE SAME NAME ALREADY EXISTS?
                // you could override it, send an error or
                // rename it, for example, if I need to upload images to an album, and for each image I also save other data, I could save the image as {image_id}.jpg using the id of the db

                Files.copy(imgContent, imgFile.toPath());
                System.out.println("Image saved correctly!");

                //resp.sendRedirect("ShowImage?filename=" + imgName);
            } catch (Exception e) {
                e.printStackTrace();
                error += ""; //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            }

            try (InputStream audioContent = taudio.getInputStream()) {
                // TODO: WHAT HAPPENS IF A FILE WITH THE SAME NAME ALREADY EXISTS?
                // you could override it, send an error or
                // rename it, for example, if I need to upload images to an album, and for each image I also save other data, I could save the image as {image_id}.jpg using the id of the db

                Files.copy(audioContent, audioFile.toPath());
                System.out.println("Track audio saved correctly!");

                //resp.sendRedirect("ShowImage?filename=" + imgName);
            } catch (Exception e) {
                e.printStackTrace();
                error += ""; //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            }


            /*Artist artist = new Artist(aName, td);
            Album album = new Album(td, aTitle, aDate, aGenre, artist,  imgOutputPath); //vedi righe sopra per l'uri
            Track track = new Track(td, tTitle, album, audioOutputPath, u);  //vedi righe sopra per l'uri
             */
            Artist artist = new Artist(artistName, td);
            Album album = new Album(td, albumTitle, albumDate, albumGenre, artist,  imgOutputPath); //vedi righe sopra per l'uri
            Track track = new Track(td, trackTitle, album, audioOutputPath, u);  //vedi righe sopra per l'uri

            try {
                td.addTrack(track.getTitle(), track.getAlbum(), track.getMp3Uri(), track.getUser());
            } catch (SQLException e){
                error += e.getMessage(); //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            } catch (Exception e) {
                error += "Something wrong during the add of the song in the database"; //Messaggio d'errore, per CAMIIIIIII
                resp.sendRedirect(error);
                return;
            }
        } else {
            error += "Missing parameters"; //Messaggio d'errore, per CAMIIIIIII
            resp.sendRedirect(error);
            return;
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
