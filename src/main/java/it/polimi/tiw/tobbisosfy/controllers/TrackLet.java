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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@MultipartConfig
@WebServlet("/Home")
public class TrackLet extends HttpServlet { //SERVLET DA SPECIFICARE E FARNE UN ALTRA SOLO PER L?ALTRA COSA playlist - track

    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;
    private User u;
    private String audioFP = "";
    private String imgFP = "";

    public TrackLet() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
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
        ArrayList<Track> songs;

        try {
            playlists = playlistDAO.getPlaylists(u);
        } catch (SQLException e) {
            error += "Error occurred while loading playlists";
            resp.sendRedirect(error);
            return;
        }

        try {
            songs = trackDAO.getTracksFromUser(u);
        } catch (SQLException e) {
            e.printStackTrace();
            error += "Error occurred while loading tracks of the user (SQL exception)";
            resp.sendRedirect(error);
            return;
        } catch (Exception e) {
            error += "Error occurred while loading tracks of the user (SQL exception)";
            resp.sendRedirect(error);
            return;
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
        String ctxPath = req.getContextPath();
        String error = ctxPath + "/ShowError?error=";

        Part trackTitle;
        Part albumDate;
        Part albumTitle;
        Part albumGenre;
        Part artistName;
        Part taudio;
        Part img;

        try {
            System.out.println("DENTRO AL TRY");
            trackTitle = req.getPart("ttitle");
            System.out.println("ttitle preso "+ new String(trackTitle.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            albumDate = req.getPart("dalbum");
            System.out.println("dalbum preso");
            albumTitle = req.getPart("talbum");
            System.out.println("talbum preso");
            albumGenre = req.getPart("g");
            System.out.println("g preso");
            artistName = req.getPart("aname");
            System.out.println("aname preso");
            taudio = req.getPart("audio");
            img = req.getPart("img");
            System.out.println("STO USCENDO DAL TRY");
        } catch (Exception e) {
            e.printStackTrace();
            error += "Error occurred while reading the form: Add a new track";
            resp.sendRedirect(error);
            return;
        }

        System.out.println("JJJJJJ");

        if(!(albumTitle == null || albumTitle.getSize() <= 0 ||
                trackTitle == null || trackTitle.getSize() <= 0 ||
                albumDate == null || albumDate.getSize() <= 0 ||
                albumGenre == null || albumGenre.getSize() <= 0 ||
                artistName == null || artistName.getSize() <= 0 ||
                /*albumTitle.isEmpty() || trackTitle.isEmpty() ||
                albumDate == 0 || albumGenre.isEmpty() ||
                artistName.isEmpty() ||*/
                img == null || img.getSize() <= 0 ||
                taudio == null || taudio.getSize() <= 0)) {
            String aTitle = new String(albumTitle.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String tTitle = new String(trackTitle.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String aName = new String(artistName.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String adate = new String(albumDate.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int aDate = Integer.parseInt(adate);
            String agenre = new String(albumGenre.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            Genre aGenre = Genre.valueOf(agenre.toUpperCase());
            System.out.println("g preso");

            // We then check the parameter is valid (in this case right format)
            String contentTypeImg = img.getContentType();
            System.out.println("Type " + contentTypeImg);

            if (!contentTypeImg.startsWith("image")) {
                error += "Image file format not permitted! Retry";
                resp.sendRedirect(error);
                return;
            }


            // We then check the parameter is valid (in this case right format)
            String contentTypeAudio = taudio.getContentType();
            System.out.println("Type " + contentTypeAudio);

            if (!contentTypeAudio.startsWith("audio")) {
                error += "Audio file format not permitted! Retry";
                resp.sendRedirect(error);
                return;
            }

            String imgName = Paths.get(img.getSubmittedFileName()).getFileName().toString();
            System.out.println("Filename: " + imgName);

            String audioName = Paths.get(taudio.getSubmittedFileName()).getFileName().toString();
            System.out.println("Filename: " + audioName);

            String imgOutputPath = imgFP + imgName; //folderPath inizialized in init
            System.out.println("Output path: " + imgOutputPath);

            File imgFile = new File(imgOutputPath);

            String audioOutputPath = audioFP + audioName; //folderPath inizialized in init
            System.out.println("Output path: " + audioOutputPath);

            File audioFile = new File(audioOutputPath);

            try (InputStream imgContent = img.getInputStream()) {
                // TODO: WHAT HAPPENS IF A FILE WITH THE SAME NAME ALREADY EXISTS?
                // you could override it, send an error or
                // rename it, for example, if I need to upload images to an album, and for each image
                //I also save other data, I could save the image as {image_id}.jpg using the id of the db

                Files.copy(imgContent, imgFile.toPath());
                System.out.println("Image saved correctly!");

            } catch (FileAlreadyExistsException e) {
                System.out.println("Image saved correctly!");
            } catch (Exception e) {
                e.printStackTrace();
                error += "Error occurred while saving the image! Retry";
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
            } catch (FileAlreadyExistsException e) {
                System.out.println("Track audio saved correctly!");
            } catch (Exception e) {
                e.printStackTrace();
                error += "Error occurred while saving the audio file! Retry";
                resp.sendRedirect(error);
                return;
            }


            Artist artist = new Artist(aName);
            Album album = new Album(aTitle, aDate, aGenre, artist,  imgName);
            Track track = new Track(tTitle, album, audioName, u);

            try {
                System.out.println("Ora aggiungo la track al server");
                td.addTrack(track.getTitle(), track.getAlbum(), track.getMp3Uri(), track.getUser());
                System.out.println("Track aggiunta corettamente al server");
            } catch (SQLException e){
                e.printStackTrace();
                error += "Error occurred while saving the track in the database (SQL exception)";
                resp.sendRedirect(error);
                return;
            } catch (Exception e) {
                error += "Error occurred while saving the track in the database";
                resp.sendRedirect(error);
                return;
            }
        } else {
            error += "Missing parameters in the 'Add a new track' form";
            resp.sendRedirect(error);
            return;
        }
        resp.sendRedirect(ctxPath+"/Home");
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
