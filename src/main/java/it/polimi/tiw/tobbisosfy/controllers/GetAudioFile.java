package it.polimi.tiw.tobbisosfy.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/GetAudioFile")
public class GetAudioFile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GetAudioFile() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = getServletContext().getInitParameter("trackpath");
        String fileName = request.getParameter("name");
        File audio;

        if (fileName == null || fileName.equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST, "Missing file name");
            return;
        }

        audio = new File(path, fileName);

        if (!audio.exists() || audio.isDirectory()) {
            System.out.println(fileName);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        Files.copy(audio.toPath(), response.getOutputStream());
    }
}
